import React, { useState, useEffect } from "react";
import {
    AppBar,
    Toolbar,
    Typography,
    IconButton,
    Box,
    Avatar,
    Menu,
    MenuItem,
} from "@mui/material";
import {
    Home as HomeIcon,
    People as FriendsIcon,
    Notifications as NotificationsIcon,
    ShoppingCart as TradeIcon,
    Receipt as InvestmentIcon,
    Public as MarketIcon,
    Settings as SettingsIcon,
    Inbox,
} from "@mui/icons-material";
import { axiosPrivate } from "../api/axios";
import { useNavigate } from "react-router-dom";
import "./Header.css";

const Header = () => {
    const [user, setUser] = useState({
        id: 0,
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        role: "",
        profilePicture: "",
    });

    const [anchorEl, setAnchorEl] = useState(null);
    const navigate = useNavigate();

    // Fetch user data on mount
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const id = localStorage.getItem("id");
                const response = await axiosPrivate.get("/api/v3/users/id:"+id);
                setUser(response.data.user);
            } catch (error) {
                console.error("Failed to fetch user data:", error.message);
            }
        };
        fetchUserData().catch(error =>
            console.error("Failed to fetch user data:", error.message)
        );
    }, []);




    // Handle logout
    async function handleLogout(e) {
        e.preventDefault();
        try {
            await axiosPrivate.post("/api/v3/auth/logout");
            console.log("Logged out successfully");
            localStorage.removeItem("accessToken");
            window.location.href = "/login";
        } catch (error) {
            console.error("Failed to log out:", error.message);
        }
    }

    // Handle profile menu open/close
    const handleMenuOpen = (event) => setAnchorEl(event.currentTarget);
    const handleMenuClose = () => setAnchorEl(null);

    return (
        <AppBar position="static">
            <Toolbar>
                {/* Logo */}
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    <img
                        id="logo"
                        src="../../aipower.png"
                        alt="AIPower Logo"
                        style={{ height: "40px", cursor: "pointer" }}
                        onClick={() => navigate("/")}
                    />
                </Typography>

                {/* Navigation Links */}
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <IconButton href="/" color="inherit" aria-label="Home">
                        <HomeIcon />
                    </IconButton>
                    <IconButton href="/friends" color="inherit" aria-label="Friends">
                        <FriendsIcon />
                    </IconButton>
                    <IconButton href="/notifications" color="inherit" aria-label="Notifications">
                        <NotificationsIcon />
                    </IconButton>
                    <IconButton href="/chat" color="inherit" aria-label="Inbox">
                        <Inbox />
                    </IconButton>
                    <IconButton href="/trade" color="inherit" aria-label="Trade">
                        <TradeIcon />
                    </IconButton>
                    <IconButton href="/investment" color="inherit" aria-label="Investment">
                        <InvestmentIcon />
                    </IconButton>
                    <IconButton href="/market" color="inherit" aria-label="Market">
                        <MarketIcon />
                    </IconButton>
                    <IconButton href="/settings" color="inherit" aria-label="Settings">
                        <SettingsIcon />
                    </IconButton>
                </Box>

                {/* User Profile */}
                <Box sx={{ display: "flex", alignItems: "center" }}>
                    <Avatar
                        src={user.profilePicture || "https://via.placeholder.com/32"}
                        alt={user.username || "User"}
                        sx={{ cursor: "pointer" }}
                        onClick={handleMenuOpen}
                    />
                    <Menu
                        anchorEl={anchorEl}
                        open={Boolean(anchorEl)}
                        onClose={handleMenuClose}
                    >
                        <MenuItem onClick={() => navigate("/profile")}>Profile</MenuItem>
                        <MenuItem onClick={() => navigate("/settings")}>Settings</MenuItem>
                        <MenuItem onClick={() => navigate("/help")}>Help</MenuItem>
                        <MenuItem onClick={() => navigate("/about")}>About</MenuItem>
                        <MenuItem onClick={handleLogout}>Logout</MenuItem>
                    </Menu>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Header;
