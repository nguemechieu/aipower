import React, { useState, useEffect } from "react";

import FriendList from "./FriendList";
import FriendRequestList from "./FriendRequestList";
import SearchBar from "./SearchBar";

const FriendsPage = () => {
    const [friends, setFriends] = useState([]);
    const [requests, setRequests] = useState([]);
    const [searchQuery, setSearchQuery] = useState("");

    useEffect(() => {
        // Mock API call to fetch friends
        const fetchFriends = async () => {
            const friendsData = [
                { id: 1, name: "Alice", profilePicture: "/path/to/image1.jpg", status: "Online" },
                { id: 2, name: "Bob", profilePicture: "/path/to/image2.jpg", status: "Offline" },
            ];
            setFriends(friendsData);
        };

        // Mock API call to fetch friend requests
        const fetchRequests = async () => {
            const requestsData = [
                { id: 3, name: "Charlie", status: "Sent you a friend request" },
                { id: 4, name: "Diana", status: "Sent you a friend request" },
            ];
            setRequests(requestsData);
        };

        fetchFriends();
        fetchRequests();
    }, []);

    const handleSearch = (query) => {
        setSearchQuery(query.toLowerCase());
    };

    const handleAcceptRequest = (id) => {
        alert(`Accepted friend request from ${id}`);
        setRequests(requests.filter((request) => request.id !== id));
    };

    const handleRejectRequest = (id) => {
        alert(`Rejected friend request from ${id}`);
        setRequests(requests.filter((request) => request.id !== id));
    };

    const filteredFriends = friends.filter((friend) =>
        friend.name.toLowerCase().includes(searchQuery)
    );

    return (
        <div style={{ padding: "20px" ,
            display: "flex",
            flexDirection: "column",
            alignItems: "center"
        }}>
            <h2>Friends Page</h2>
            <SearchBar onSearch={handleSearch} />
            <FriendList friends={filteredFriends} />
            <FriendRequestList
                requests={requests}
                onAccept={handleAcceptRequest}
                onReject={handleRejectRequest}
            />
        </div>
    );
};

export default FriendsPage;
