import {useReactFlow} from "@xyflow/react";
import {v4} from "uuid";
import {useState} from "react";

function AddNode(props){



    return(
        <>
            <button onClick={props.addQueue}>Queue</button>
            <button onClick={props.addMachine}>Machine</button>
            <button onClick={props.simulate}>Simulate</button>
        </>
    )
}

export default AddNode