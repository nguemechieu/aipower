import React, { useState, useEffect } from "react";
import {
    AppBar,
    Toolbar,
    Typography,
    IconButton,
    Box,
    Avatar,
    MenuItem,
    Menu,
    Button,
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
import { useNavigate } from "react-router-dom";
import useAuth from "../hooks/useAuth";
import useAxiosPrivate from "../hooks/useAxiosPrivate";

const Header = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [anchorEl, setAnchorEl] = useState(null);
    const navigate = useNavigate();
    const {auth }= useAuth();
    const axiosPrivate = useAxiosPrivate();

    // Check user authentication status
    useEffect(() => {
        const checkAuth = async () => {
            try {
                setIsLoggedIn(!!auth?.id && auth.id > 1 &&auth.accessToken
                    &&auth.refreshToken

                ); // Ensure proper auth state
            } catch (error) {
                console.error("Error checking auth status:", error);
                setIsLoggedIn(false);
            }
        };
        checkAuth().catch((error) =>
            console.error("Error checking auth status:", error)
        );
    }, [auth]);

    // Handle profile menu open/close
    const handleMenuOpen = (event) => setAnchorEl(event.currentTarget);
    const handleMenuClose = () => setAnchorEl(null);

    // Logout handler
    const handleLogout = async () => {
        try {
            // Clear tokens or call logout endpoint
            await axiosPrivate.post("/api/v3/users/logout");
            console.log("Logged out successfully");
            localStorage.removeItem("accessToken"); // Clear token (if stored locally)
            setIsLoggedIn(false); // Update login state
            navigate("/login"); // Redirect to login page
        } catch (error) {
            console.error("Error during logout:", error);
        }
    };

    return (
        <AppBar position="static">
            <Toolbar>
                {/* Logo */}
                <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
                    <img
                        id="logo"
                        src="../assets/images/logo512.png"
                        alt="AIPower"
                        style={{ height: "40px", cursor: "pointer" }}
                        onClick={() => navigate("/")}
                    />
                </Typography>

                {/* Navigation Links */}
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    <IconButton onClick={() => navigate("/")} color="inherit" aria-label="Home">
                        <HomeIcon />
                    </IconButton>
                    <IconButton onClick={() => navigate("/friends")} color="inherit" aria-label="Friends">
                        <FriendsIcon />
                    </IconButton>
                    <IconButton onClick={() => navigate("/notifications")} color="inherit" aria-label="Notifications">
                        <NotificationsIcon />
                    </IconButton>
                    <IconButton onClick={() => navigate("/chat")} color="inherit" aria-label="Inbox">
                        <Inbox />
                    </IconButton>
                    <IconButton onClick={() => navigate("/trade")} color="inherit" aria-label="Trade">
                        <TradeIcon />
                    </IconButton>
                    <IconButton onClick={() => navigate("/investment")} color="inherit" aria-label="Investment">
                        <InvestmentIcon />
                    </IconButton>
                    <IconButton onClick={() => navigate("/market")} color="inherit" aria-label="Market">
                        <MarketIcon />
                    </IconButton>
                    <IconButton onClick={() => navigate("/settings")} color="inherit" aria-label="Settings">
                        <SettingsIcon />
                    </IconButton>
                </Box>

                {/* User Profile */}
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                    {isLoggedIn ? (
                        <>
                            <Avatar
                                src="/assets/images/profile.png"
                                alt="User"
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
                            </Menu>
                            {/* Logout Button */}
                            <Button color="inherit" onClick={handleLogout}>
                                Logout
                            </Button>
                        </>
                    ) : (
                        <Button color="inherit" onClick={() => navigate("/login")}>
                            Login
                        </Button>
                    )}
                </Box>
            </Toolbar>
        </AppBar>
    );
};

export default Header;
