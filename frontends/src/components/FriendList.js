import React from "react";

const FriendList = ({ friends }) => {
    return (
        <div>
            <h3>Your Friends</h3>
            <ul style={{ listStyleType: "none", padding: 0 }}>
                {friends.map((friend) => (
                    <li
                        key={friend.id}
                        style={{
                            display: "flex",
                            alignItems: "center",
                            marginBottom: "10px",
                            border: "1px solid #ddd",
                            padding: "10px",
                            borderRadius: "5px",
                        }}
                    >
                        <img
                            src={friend.profilePicture}
                            alt={`${friend.name}'s profile`}
                            style={{
                                width: "50px",
                                height: "50px",
                                borderRadius: "50%",
                                marginRight: "10px",
                            }}
                        />
                        <div>
                            <strong>{friend.name}</strong>
                            <p>{friend.status}</p>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default FriendList;
