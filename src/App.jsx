import {useCallback, useEffect, useState} from 'react'
import './App.css'
import '@xyflow/react/dist/style.css'
import {addEdge, Background, Controls, ReactFlow, useEdgesState, useNodes, useNodesState} from "@xyflow/react";
import QNode from "./QNode.jsx";
import MNode from "./MNode.jsx";
import {v4} from "uuid";
import AddNode from "./AddNode.jsx";
import AnimatedEdge from './AnimatedEdge';
import axios from "axios";
import {Stomp} from "@stomp/stompjs";
import SockJS from 'sockjs-client/dist/sockjs';

function App() {
    const [sim,setSim] = useState(false);

    const initialNodes = [{
        id: '0',
        data:{
            amount:"Q0"
        },
        position:{x:100, y:100},
        type:'qNode',
        },
        // {
        //     id: v4(),
        //     data:{
        //         amount: "M",
        //
        //     },
        //     position:{x:0, y:0},
        //     type: 'addNode',
        // },
    ]
    const initialEdges = []
    const [nodes,setNodes, onNodesChange] = useNodesState(initialNodes)
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges)

    const onConnect = useCallback((connection) => {
        const edge = {...connection, animated:true, id: `${v4()}`,markerEnd: { type: 'arrow', color: '#f00' }, type: sim?'animated': 'default'};
        setEdges((prevState) => addEdge(edge, prevState))
        console.log(nodes)
        console.log(edges)

    },[])

    const edgeTypes = {
        'animated': AnimatedEdge,
    };

    const nodeTypes = {
        'qNode': QNode,
        'mNode': MNode,

    }

        const [status, setStatus] = useState('');
    const [message, setMessage] = useState('');
    const [response, setResponse] = useState('');

    useEffect(() => {
        // Establish WebSocket connection
        const socket = new SockJS('http://localhost:8080/ws'); // Adjust the URL as needed
        const stompClient = Stomp.over(socket);

        // Connect to the WebSocket
        stompClient.connect({}, (frame) => {
            console.log('Connected: ' + frame);

            // Subscribe to the /topic/status topic
            stompClient.subscribe('/topic/status', (msg) => {
                console.log("message")
                console.log(msg)
                setStatus(msg.body);
                const temp = JSON.parse(msg.body);
                console.log(JSON.parse(msg.body));
                const newNodes = nodes.map(
                    (node) => ( node.id === temp.id)? {...node,data:{...node.data,color:temp.color} }: node
                )
                console.log(newNodes)
                 setNodes(newNodes)
            });
        });

        // Clean up the connection when the component is unmounted
        return () => {
            if (stompClient) {
                stompClient.disconnect();
            }
        };
    }, [setNodes]);


    async function simulate(){
        setEdges(
            prevEdges => prevEdges.map(prevEdge => ({...prevEdge,type:sim?'default': 'animated'}))
        )
        setSim(prevSim=>!prevSim)
        console.log(edges)
        console.log(nodes)

        try {
            // nodes.map((node) =>{
            //     if(node.type === 'qNode'){
            //         axios.post(`http://localhost:8080/api/addQueue?id=${node.id}`)
            //     }else if(node.type === 'mNode'){
            //         axios.post(`http://localhost:8080/api/addMachine?id=${node.id}`)
            //     }
            //     console.log(node)
            // })
            // edges.map((edge) =>{
            //     const source = nodes.filter(node => node.id === edge.source)[0].type
            //     const destination = nodes.filter(node => node.id === edge.target)[0].type
            //     console.log("source")
            //     console.log(source)
            //     console.log("destination")
            //     console.log(destination)
            //     if(source ==="qNode" && destination === "mNode"){
            //         axios.post(`http://localhost:8080/api/connectQueueToMachine?fromId=${edge.source}&toId=${edge.target}`)
            //
            //
            //     }
            //     else if(source === "mNode" && destination === "qNode"){
            //         axios.post(`http://localhost:8080/api/connectMachineToQueue?fromId=${edge.source}&toId=${edge.target}`)
            //
            //     }
            //     else {
            //         console.log("invalid connection")
            //     }
            // });
            const socket = new SockJS('http://localhost:8080/ws');
            const stompClient = Stomp.over(socket);

            let nodeIds =[[],[],[],[]];

            nodes.map(edge =>{
                if(edge.type === 'mNode'){
                    nodeIds[0] = [...nodeIds[0],edge.id]
                }
                if(edge.type === 'qNode'){
                    nodeIds[1] = [...nodeIds[1],edge.id]

                }

            })

            edges.map((edge) =>{
                const source = nodes.filter(node => node.id === edge.source)[0].type
                const destination = nodes.filter(node => node.id === edge.target)[0].type
                console.log("source")
                console.log(source)
                console.log("destination")
                console.log(destination)
                if(source ==="qNode" && destination === "mNode"){ // from Queue to machine
                    nodeIds[2] = [...nodeIds[2],edge.source,edge.target]

                }
                else if(source === "mNode" && destination === "qNode"){
                    nodeIds[3] = [...nodeIds[3],edge.source,edge.target]

                }
                else {
                    console.log("invalid connection")
                }
            });

            stompClient.connect({}, () => {
                stompClient.send('/app/status', {},JSON.stringify(nodeIds));
            });
            }catch (e){
                console.log(e)
            }
    }



    function location(){
        return Math.random() *300
    }

    const newQueue = {
        id: v4(),
        data:{
            amount:"Q",
            color: "transparent"
        },
        position:{ x: location(), y:location() },
        type: 'qNode',
    }
    const newMachine = {
        id: v4(),
        data:{
            amount:"M",
            color: "transparent"
        },
        position:{ x: location(), y:location() },
        type: 'mNode',

    }

    function addQueue(){

        setNodes(prev => [...prev, newQueue])
    }
    function addMachine(){

        setNodes(prev => [...prev, newMachine])
    }


  return (
    <>
        <AddNode
            simulate={simulate}
            addMachine={addMachine}
            addQueue={addQueue}
        />
      <div style={{height:'1000px', width:'1500px'}}>
          <ReactFlow
              height={"500px"}
              width={"500px"}
              colorMode={"system"}
              nodes={nodes}
              fitView={true}
              edges={edges}
              onNodesChange={onNodesChange}
              onEdgesChange={onEdgesChange}
              onConnect={onConnect}
              nodeTypes={nodeTypes}
              edgeTypes={edgeTypes}
          >

              <Background/>
              <Controls/>

          </ReactFlow>
      </div>
    </>
  )
}

export default App












// import React, { useEffect, useState } from 'react';
// import SockJS from 'sockjs-client/dist/sockjs';
// import { Stomp } from '@stomp/stompjs';
//
// const App = () => {
//     const [status, setStatus] = useState('');
//     const [message, setMessage] = useState('');
//     const [response, setResponse] = useState('');
//
//     useEffect(() => {
//         // Establish WebSocket connection
//         const socket = new SockJS('http://localhost:8080/ws'); // Adjust the URL as needed
//         const stompClient = Stomp.over(socket);
//
//         // Connect to the WebSocket
//         stompClient.connect({}, (frame) => {
//             console.log('Connected: ' + frame);
//
//             // Subscribe to the /topic/status topic
//             stompClient.subscribe('/topic/status', (msg) => {
//                 console.log("message")
//
//                 console.log(msg)
//                 setStatus(msg.body);
//             });
//         });
//
//         // Clean up the connection when the component is unmounted
//         return () => {
//             if (stompClient) {
//                 stompClient.disconnect();
//             }
//         };
//     }, []);
//
//     const sendMessage = () => {
//         const socket = new SockJS('http://localhost:8080/ws');
//         const stompClient = Stomp.over(socket);
//
//         stompClient.connect({}, () => {
//             stompClient.send('/app/status', {});
//         });
//     };
//
//     return (
//         <div>
//             <h1>WebSocket Demo</h1>
//             <input
//                 type="text"
//                 value={message}
//                 onChange={(e) => setMessage(e.target.value)}
//                 placeholder="Enter your message"
//             />
//             <button onClick={sendMessage}>Send Message</button>
//             <button onClick={()=>console.log(JSON.parse(status))}>print</button>
//             <p>Response: {response}</p>
//             <p>Status: {status}</p>
//         </div>
//     );
// };
//
// export default App;


