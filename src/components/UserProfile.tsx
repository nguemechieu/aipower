import React, { useEffect, useState } from 'react';
import {axiosPrivate} from "../api/axios";

const UserProfile = () => {
    const [userInfo, setUserInfo] = useState(null);

    useEffect(() => {  const code = new URLSearchParams(window.location.search).get('code');
        // Retrieve user information from your backend
        const fetchUserInfo = async () => {
            const response = await axiosPrivate.post('/api/v3/auth/google/callback?code=' + new URLSearchParams(window.location.search).get('code'),
                {
                    code: code
                });
            const data = await response.data
            setUserInfo(data);
        };


        if (code) {
            fetchUserInfo().then(r => {
                console.log(r);
            });
        }
    }, []);

    if (!userInfo) return <div>Loading...</div>;

    return (
        <div>
            <h1>User Profile</h1>
            <p>Name: {userInfo.name}</p>
            <p>Email: {userInfo.email}</p>
            <img src={userInfo.picture} alt="User Profile" />
            <p>User ID: {userInfo.sub}</p>
        </div>
    );
};

export default UserProfile;
