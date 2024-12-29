import {Handle, useReactFlow} from "@xyflow/react";


function QNode(props) {

    return (
        <div className="q-node">
            <div style={{color: "white"}}>
                {props.data.amount}
            </div>

            <Handle type={"source"} position={"right"}/>
            <Handle type={"target"} position={"left"}/>
        </div>
    )
}

export default QNode;
