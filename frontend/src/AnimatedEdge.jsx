import React from 'react';
import {BaseEdge, getBezierPath, getSmoothStepPath} from '@xyflow/react';
import './Products.svg'

function AnimatedEdge({
                         id,
                         sourceX,
                         sourceY,
                         targetX,
                         targetY,
                         sourcePosition,
                         targetPosition,


                         }) {
    const [edgePath] = getBezierPath({
        sourceX,
        sourceY,
        sourcePosition,
        targetX,
        targetY,
        targetPosition,

    });

    return (
        <>
            <BaseEdge id={id} path={edgePath} />

            <circle r="10" fill="#ff0073">
                <animateMotion dur="1s" repeatCount="indefinite" path={edgePath} />
            </circle>
        </>
    );
}




export default AnimatedEdge;