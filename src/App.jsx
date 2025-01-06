import {useCallback, useState} from 'react'
import './App.css'
import '@xyflow/react/dist/style.css'
import {addEdge, Background, Controls, ReactFlow, useEdgesState, useNodes, useNodesState} from "@xyflow/react";
import QNode from "./QNode.jsx";
import MNode from "./MNode.jsx";
import {v4} from "uuid";
import AddNode from "./AddNode.jsx";
import AnimatedEdge from './AnimatedEdge';
import axios from "axios";
import { useEffect } from 'react';
import SockJS from 'sockjs-client/dist/sockjs';


function App() {
    const [webSocket, setWebSocket] = useState(null);
   
    
    const initialNodes = [{
        id: '0',
        data:{
            amount:"Q0",
            num:0
        },
        position:{x:100, y:100},
        type:'qNode',
        },

    ]
 
    const initialEdges = []
    const [nodes,setNodes, onNodesChange] = useNodesState(initialNodes);
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges);
   

    useEffect(() => {
        const socket = new WebSocket('ws://localhost:8080/simulation'); 
        socket.onopen = () => {
            console.log('WebSocket connected');
        };
    
        socket.onmessage = (event) => {
            try {
                const message = JSON.parse(event.data);
                console.log('Received data:', message);
    
                setNodes((prevNodes) =>
                    prevNodes.map((node) => {
                        if (node.id === message.prevID) {
                            return {
                                ...node,
                                data: {
                                    ...node.data,
                                    num: message.prevsize, // Update `num` for `prevID`
                                },
                            };
                        } else if (node.id === message.nextID) {
                            return {
                                ...node,
                                data: {
                                    ...node.data,
                                    num: message.nextsize, // Update `num` for `nextID`
                                },
                            };
                        }else if(node.id === message.machineid){
                            return{
                            ...node,
                            data: {
                                ...node.data,
                                color: message.color, // Update the color property
                            },
                         };
                      
                        }
                        return node;
                    })
                );
            } catch (error) {
                console.error('Error parsing WebSocket message:', error);
            }
        };
    
        socket.onclose = () => {
            console.log('WebSocket closed');
        };
    
        setWebSocket(socket);
        return () => {
            socket.close();
        };
    }, []);
    
    const [sim,setSim] = useState(false);

 
    
    
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


    async function simulate(){
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            const simulationData = {
              nodes,
              edges,
            };
            webSocket.send(JSON.stringify(simulationData));
            console.log('Sent simulation data:', simulationData);
          } else {
            console.error('WebSocket is not open. Cannot sent data.');
        }
        setEdges(
            prevEdges => prevEdges.map(prevEdge => ({...prevEdge,type:sim?'bezier': 'animated'}))
        )
        setSim(prevSim=>!prevSim)
        console.log(edges)
        console.log(nodes)


    }

    async function resimulate(){
        if (webSocket && webSocket.readyState === WebSocket.OPEN) {
            webSocket.send("true");
           // console.log('Sent simulation data:', simulationData);
          } else {
            console.error('WebSocket is not open. Cannot sent data.');
        }
        
        setSim(prevSim=>!prevSim)
        console.log(edges)
        console.log(nodes)
    }



    function location(){
        return Math.random() *10
    }

    const newQueue = {
        id: v4(),
        data:{
            amount:"Q",
        },
        position:{ x: location(), y:location() },
        type: 'qNode',
    }
    const newMachine = {
        id: v4(),
        data:{
            amount:"M",
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
            resimulate={resimulate}
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

