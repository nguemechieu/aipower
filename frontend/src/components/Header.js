import React, { useEffect, useState } from "react";
import {
    AppBar,
    Toolbar,
    Typography,
    IconButton,
    Box,
    Avatar,
    TextField,
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
    Search as SearchIcon,
} from "@mui/icons-material";
import { axiosPrivate } from "../api/axios";

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

    const [error, setError] = useState("");
    const [anchorEl, setAnchorEl] = useState(null);

    // Fetch user data from the backend
    const fetchUserData = async () => {
        const id = localStorage.getItem("id");
        try {
            const response = await axiosPrivate.get(`/api/v3/users/id:${id}`, {
                withCredentials: true,
            });

            if (response.status === 200) {
                setUser(response.data);
            } else {
                setError(`Failed to fetch user data: ${response.statusText}`);
                console.error("Error fetching user data:", response.statusText);
            }
        } catch (err) {
            setError("An error occurred while fetching user data.");
            console.error("Error fetching user data:", err.message);
        }
    };

    useEffect(() => {
        fetchUserData().then(
            (r)=>console.log(
                `User data fetched successfully. User: ${user.username}`
            )
        ); // Fetch data on component mount
    }, []);

    // Handle profile menu open/close
    const handleMenuOpen = (event) => setAnchorEl(event.currentTarget);
    const handleMenuClose = () => setAnchorEl(null);

    return (
        <AppBar position="static" sx={{ backgroundColor: "#0a3463" }}>
            <Toolbar>
                {/* Logo */}
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    AIPower
                </Typography>

                {/* Navigation Links */}
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <IconButton href="/" color="inherit">
                        <HomeIcon />
                    </IconButton>
                    <IconButton href="/friends" color="inherit">
                        <FriendsIcon />
                    </IconButton>
                    <IconButton href="/chat" color="inherit">
                        <NotificationsIcon />
                    </IconButton>
                    <IconButton href="/trade" color="inherit">
                        <TradeIcon />
                    </IconButton>
                    <IconButton href="/investment" color="inherit">
                        <InvestmentIcon />
                    </IconButton>
                    <IconButton href="/market" color="inherit">
                        <MarketIcon />
                    </IconButton>
                    <IconButton href="/settings" color="inherit">
                        <SettingsIcon />
                    </IconButton>
                </Box>

                {/* Search Bar */}
                <Box sx={{ flexGrow: 1, mx: 2 }}>
                    <TextField
                        variant="outlined"
                        size="small"
                        placeholder="Search..."
                        InputProps={{
                            startAdornment: (
                                <SearchIcon sx={{ mr: 1, color: "gray" }} />
                            ),
                        }}
                        fullWidth
                        onChange={(e) => console.log("Search Input:", e.target.value)}
                    />
                </Box>

                {/* User Profile */}
                <Box sx={{ display: "flex", alignItems: "center" }}>
                    <Typography sx={{ mr: 2 }}>
                        {user.firstName ? `${user.firstName} ${user.lastName}` : "John Doe"}
                    </Typography>
                    <Avatar
                        src={user.profilePicture || "https://via.placeholder.com/32"}
                        alt="User Profile"
                        sx={{ cursor: "pointer" }}
                        onClick={handleMenuOpen}
                    />
                    <Menu
                        anchorEl={anchorEl}
                        open={Boolean(anchorEl)}
                        onClose={handleMenuClose}
                    >
                        <MenuItem onClick={handleMenuClose}>Profile</MenuItem>
                        <MenuItem onClick={handleMenuClose}>Logout</MenuItem>
                    </Menu>
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Header;
