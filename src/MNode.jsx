import {Handle, useReactFlow} from "@xyflow/react";
import {IoMdClose} from "react-icons/io";


function MNode(props) {
    return (
        <div
            className="m-node"
            style={{
                backgroundColor: props.data.color || "gray", // Default color if color is not set
                border: "1px solid white",
                borderRadius: "5px",
                padding: "10px",
            }}
        >
            <div style={{ color: "white", textAlign: "center" }}>
                {props.data.amount}
            </div>

            <Handle type={"source"} position={"right"} />
            <Handle type={"target"} position={"left"} />
        </div>
    );
}

export default MNode;
