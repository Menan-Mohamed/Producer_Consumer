import {Handle, useReactFlow} from "@xyflow/react";
import {IoMdClose} from "react-icons/io";

function MNode(props) {

    return (
        <div className="m-node" style={{backgroundColor: props.data.color}}>
            <div style={{color: "white"}}>
                {props.data.amount}
            </div>

            <Handle type={"source"} position={"right"}/>
            <Handle type={"target"} position={"left"}/>
        </div>
    )
}

export default MNode;
