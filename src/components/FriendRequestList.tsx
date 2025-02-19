import React from "react";

const FriendRequestList = ({ requests, onAccept, onReject }: any) => (
  <div>
    <h3>Friend </h3>
    <ul style={{ listStyleType: "none", padding: 0 }}>
      {requests.map(
        (request: {
          id: React.Key | null | undefined;
          name:
            | string
            | number
            | boolean
            | React.ReactElement<any, string | React.JSXElementConstructor<any>>
            | Iterable<React.ReactNode>
            | React.ReactPortal
            | null
            | undefined;
          status:
            | string
            | number
            | boolean
            | React.ReactElement<any, string | React.JSXElementConstructor<any>>
            | Iterable<React.ReactNode>
            | React.ReactPortal
            | null
            | undefined;
        }) => (
          <li
            key={request.id}
            style={{
              display: "flex",
              alignItems: "center",
              justifyContent: "space-between",
              marginBottom: "10px",
              border: "1px solid #ddd",
              padding: "10px",
              borderRadius: "5px",
            }}
          >
            <div>
              <strong>{request.name}</strong>
              <p>{request.status}</p>
            </div>
            <div>
              <button onClick={() => onAccept(request.id)}>Accept</button>
              <button onClick={() => onReject(request.id)}>Reject</button>
              <button onClick={() => onAccept(request.id)}>Delete</button>
            </div>
          </li>
        ),
      )}
    </ul>
  </div>
);

export default FriendRequestList;
