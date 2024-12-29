import {useReactFlow} from "@xyflow/react";
import {v4} from "uuid";

function AddNode(){
    const {setNodes} = useReactFlow()

    function location(){
        return Math.random() *300
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

    return(
        <>
            <button onClick={addQueue}>Queue</button>
            <button onClick={addMachine}>Machine</button>
        </>
    )
}

export default AddNode