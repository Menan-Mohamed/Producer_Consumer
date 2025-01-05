import { Handle } from "@xyflow/react";

function QNode(props) {
    return (
        <div
            className="q-node"
            style={{
                backgroundColor: "#333", // Example styling for contrast
                color: "white",
                border: "1px solid white",
                borderRadius: "5px",
                padding: "5px",
                textAlign: "center",
            }}
        >
            <div>
                {props.data.amount}
            </div>
            <p>{props.data.num || 0}</p>
            <Handle type={"source"} position={"right"} />
            <Handle type={"target"} position={"left"} />
        </div>
    );
}

export default QNode;
