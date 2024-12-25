import React, { useState, useEffect } from "react";


import FriendRequestList from "./FriendRequestList";
import SearchBar from "./SearchBar";
import {axiosPrivate} from "../api/axios";


const FriendsPage = () => {

  const [requests, setRequests] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const onSearch = (search: string) => {
    onSearch(searchQuery);
  }

  useEffect(() => {
    // Mock API call to fetch friends
// Mock API call to fetch friend requests
    const fetchRequests = async () => {
      const fetchRequests = async () => {
        try {
          const response = await axiosPrivate.get("/api/v3/friend-requests");
          setRequests(response.data);
        } catch (error) {
          console.error("Error fetching friend requests: ", error);
        }
      };
      fetchRequests().catch(
          (error) => console.error("Error fetching friend requests: ", error)
      );

    };


    fetchRequests().then(r => console.log("Friend data fetched"));
  }, []);

  const handleSearch = (query: string) => {
    setSearchQuery(query.toLowerCase());
  };

  const handleAcceptRequest = (id: bigint) => {
    alert(`Accepted friend request from ${id}`);
    setRequests(requests.filter((request) => request.id !== id));
  };

  const handleRejectRequest = (id: any) => {
    alert(`Rejected friend request from ${id}`);
    setRequests(requests.filter((request) => request.id !== id));
  };
  const [friends, setFriends] = useState([]);
  useEffect(() => {
      const fetchFriends = async () => {
          try {
              const response = await axiosPrivate.get("/api/v3/friends");
              setFriends(response.data);
          } catch (error) {
              console.error("Error fetching friends: ", error);
          }
      };
      fetchFriends().catch((error) => console.error("Error fetching friends: ", error));
  }, []);



  return (
    <div
      style={{
        padding: "20px",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
      }}
    >
      <h2>Friends Page</h2>
      <SearchBar onSearch={handleSearch} />

      <FriendRequestList
        requests={requests}
        onAccept={handleAcceptRequest}
        onReject={handleRejectRequest}
      />
    </div>
  );
};

export default FriendsPage;
