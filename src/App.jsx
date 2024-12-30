import {useCallback, useState} from 'react'
import './App.css'
import '@xyflow/react/dist/style.css'
import {addEdge, Background, Controls, ReactFlow, useEdgesState, useNodes, useNodesState} from "@xyflow/react";
import QNode from "./QNode.jsx";
import MNode from "./MNode.jsx";
import {v4} from "uuid";
import AddNode from "./AddNode.jsx";
import AnimatedEdge from './AnimatedEdge';

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
        {
            id: v4(),
            data:{
                amount: "M",
            },
            position:{x:0, y:0},
            type: 'addNode',
        },
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
        'addNode': AddNode
    }


  return (
    <>
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
//             stompClient.send('/app/status', {}, message);
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
//             <p>Response: {response}</p>
//             <p>Status: {status}</p>
//         </div>
//     );
// };
//
// export default App;


