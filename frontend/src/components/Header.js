import useAuth from "../hooks/useAuth";
import {useEffect, useState} from "react";
import {NavLink} from "react-router-dom";
import "./Header.css";

const Header=()=>{

    const {user,logout} = useAuth();
    const [isAuthenticated, setIsAuthenticated] = useState(false);

    useEffect(() => {
        setIsAuthenticated(Boolean(user));
    })

    return (
        <header>
            <h1>AI Power</h1>
            {isAuthenticated && (
                <nav>
                    <ul>
                        <li><a href="/dashboard">Dashboard</a></li>
                        <li><a href="/about">About</a></li>
                        <li><a href="/user-management">User Management</a></li>
                        <li><button onClick={logout}>Logout</button></li>
                    </ul>
                </nav>
            )}
            {!isAuthenticated && (
                <div className="row">

                    <div className="col-md-8">




                <nav>
                    <ul>
                        <li><NavLink to="/login">Login</NavLink></li>
                        <li><NavLink to="/register">Register</NavLink></li>
                        <li><NavLink to="/forgot-password">Forgot Password</NavLink></li>
                        <li><NavLink to="/terms-of-service">Terms of Service</NavLink></li>
                        <li><NavLink to="/help">Help</NavLink></li>
                        <li><NavLink to="/about">About</NavLink></li>
                    </ul>
                </nav>   </div>
                </div>
            )}

        </header>


    );
};


export default Header;