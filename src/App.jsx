import {useCallback, useState} from 'react'
import './App.css'
import '@xyflow/react/dist/style.css'
import {addEdge, Background, Controls, ReactFlow, useEdgesState, useNodes, useNodesState} from "@xyflow/react";
import QNode from "./QNode.jsx";
import MNode from "./MNode.jsx";
import {v4} from "uuid";
import AddNode from "./AddNode.jsx";

function App() {
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
                amount: "M"
            },
            position:{x:0, y:0},
            type: 'addNode',

        },
    ]
    const initialEdges = [{
        id: '3',
        source: '1',
        target: '2',
        markerEnd: { type: 'arrow', color: '#f00' },
        animated: true,
    }]
    const [nodes,setNodes, onNodesChange] = useNodesState(initialNodes)
    const [edges, setEdges, onEdgesChange] = useEdgesState(initialEdges)

    const onConnect = useCallback((connection) => {
        const edge = {...connection, animated:true, id: `${v4()}`,markerEnd: { type: 'arrow', color: '#f00' }}
        setEdges((prevState) => addEdge(edge, prevState))

    },[])

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
          >

              <Background/>
              <Controls/>

          </ReactFlow>
      </div>
    </>
  )
}

export default App
