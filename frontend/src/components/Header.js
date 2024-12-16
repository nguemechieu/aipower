import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Header.css';
import useAuth from '../hooks/useAuth';
import { Button } from '@mui/material';
import { axiosPrivate } from '../api/axios';

const Header = () => {
    const {auth} = useAuth(); // Assuming auth contains user details like { username: 'JohnDoe' }
    const navigate = useNavigate();

    const logout = async (e) => {
        e.preventDefault();
        try {
            const res = await axiosPrivate.post('/auth/logout'); // Use POST if logout is an action
            if (res.status === 200) {
                navigate('/', { replace: true });
            } else {
                console.error('Error while logging out');
            }
        } catch (error) {
            console.error('Logout failed:', error);
        }
    };

    return (
        <header className="header">
            <div className="logo">
                <img src="./aipower.ico" alt="AiPower" />

            </div>
            {auth?.username ? (
                <>
                    <div className="user-info">
                        <span>Welcome, {auth.username}!</span>
                    </div>
                    <nav>
                        <ul>
                            <li><Link to="/dashboard">Dashboard</Link></li>
                            <li><Link to="/market">Market</Link></li>
                            <li><Link to="/profile">Profile</Link></li>
                            <li><Link to="/news">News</Link></li>
                            <li><Link to="/investment">Investment</Link></li>
                            <li><Link to="/chat">Chat</Link></li>
                            <li><Link to="/trade">Trade</Link></li>
                        </ul>
                    </nav>
                    <Button onClick={logout} variant="contained" color="primary">Logout</Button>
                </>
            ) : (
                <nav>

                        <li><Link to="/login">Login</Link></li>


                </nav>
            )}
        </header>
    );
};

export default Header;
