import React, { useState } from "react";
import {
    Box,
    Typography,
    TextField,
    Button,
    Checkbox,
    FormControlLabel,
    FormGroup,
    Select,
    MenuItem,
    Paper,
    List,
    ListItem,
    ListItemText,
    IconButton,
    Tooltip, Grid2,
} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";
import AddCircleOutlineIcon from "@mui/icons-material/AddCircleOutline";
import SaveIcon from "@mui/icons-material/Save";


const Settings = () => {
    // State declarations
    const [username, setUsername] = useState("JohnDoe123");
    const [email, setEmail] = useState("johndoe@example.com");
    const [password, setPassword] = useState("");
    const [theme, setTheme] = useState("light");
    const [twoFactor, setTwoFactor] = useState(false);
    const [emailNotifications, setEmailNotifications] = useState(true);
    const [smsNotifications, setSmsNotifications] = useState(false);
    const [pushNotifications, setPushNotifications] = useState(true);
    const [subscriptions, setSubscriptions] = useState([
        { id: 1, name: "Premium Membership" },
        { id: 2, name: "Newsletter" },
    ]);
    const [newSubscription, setNewSubscription] = useState("");

    // Handlers
    const handleSaveProfile = (e) => {
        e.preventDefault();
        alert("Profile updated successfully!");
    };

    const handleSaveSecurity = (e) => {
        e.preventDefault();
        alert("Security settings updated successfully!");
    };

    const handleSavePreferences = () => {
        alert("Preferences updated successfully!");
    };

    const handleSaveNotifications = (e) => {
        e.preventDefault();
        alert("Notification preferences updated successfully!");
    };

    const handleAddSubscription = (e) => {
        e.preventDefault();
        if (newSubscription.trim()) {
            setSubscriptions([
                ...subscriptions,
                { id: Date.now(), name: newSubscription },
            ]);
            setNewSubscription("");
        }
    };

    const handleRemoveSubscription = (id) => {
        setSubscriptions(subscriptions.filter((sub) => sub.id !== id));
    };

    return (
        <Box sx={{ padding: 4 }}>
            <Typography variant="h4" gutterBottom>
                Settings
            </Typography>

            {/* Profile Section */}
            <Paper sx={{ padding: 4, marginBottom: 4 }} elevation={3}>
                <Typography variant="h5" gutterBottom>
                    Profile
                </Typography>
                <Grid2 container spacing={3}>
                    <Grid2 xs={12} sm={6}>
                        <TextField
                            label="Username"
                            fullWidth
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                        />
                    </Grid2>
                    <Grid2 xs={12} sm={6}>
                        <TextField
                            label="Email"
                            fullWidth
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </Grid2>
                </Grid2>
                <Button
                    variant="contained"
                    color="primary"
                    sx={{ marginTop: 2 }}
                    startIcon={<SaveIcon />}
                    onClick={handleSaveProfile}
                >
                    Save Profile
                </Button>
            </Paper>

            {/* Security Section */}
            <Paper sx={{ padding: 4, marginBottom: 4 }} elevation={3}>
                <Typography variant="h5" gutterBottom>
                    Security
                </Typography>
                <TextField
                    label="Change Password"
                    fullWidth
                    type="password"
                    placeholder="Enter new password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    sx={{ marginBottom: 2 }}
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={twoFactor}
                            onChange={(e) => setTwoFactor(e.target.checked)}
                        />
                    }
                    label="Enable Two-Factor Authentication"
                />
                <Button
                    variant="contained"
                    color="primary"
                    sx={{ marginTop: 2 }}
                    startIcon={<SaveIcon />}
                    onClick={handleSaveSecurity}
                >
                    Save Security Settings
                </Button>
            </Paper>

            {/* Notifications Section */}
            <Paper sx={{ padding: 4, marginBottom: 4 }} elevation={3}>
                <Typography variant="h5" gutterBottom>
                    Notifications
                </Typography>
                <FormGroup>
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={emailNotifications}
                                onChange={(e) => setEmailNotifications(e.target.checked)}
                            />
                        }
                        label="Email Notifications"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={smsNotifications}
                                onChange={(e) => setSmsNotifications(e.target.checked)}
                            />
                        }
                        label="SMS Notifications"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={pushNotifications}
                                onChange={(e) => setPushNotifications(e.target.checked)}
                            />
                        }
                        label="Push Notifications"
                    />
                </FormGroup>
                <Button
                    variant="contained"
                    color="primary"
                    sx={{ marginTop: 2 }}
                    startIcon={<SaveIcon />}
                    onClick={handleSaveNotifications}
                >
                    Save Notification Preferences
                </Button>
            </Paper>

            {/* Preferences Section */}
            <Paper sx={{ padding: 4, marginBottom: 4 }} elevation={3}>
                <Typography variant="h5" gutterBottom>
                    Preferences
                </Typography>
                <Select
                    value={theme}
                    onChange={(e) => setTheme(e.target.value)}
                    fullWidth
                    sx={{ marginBottom: 2 }}
                 variant={"filled"}>
                    <MenuItem value="light">Light</MenuItem>
                    <MenuItem value="dark">Dark</MenuItem>
                    <MenuItem value="custom">Custom</MenuItem>
                    <MenuItem value="system">System</MenuItem>
                </Select>
                <Button
                    variant="contained"
                    color="primary"
                    startIcon={<SaveIcon />}
                    onClick={handleSavePreferences}
                >
                    Save Preferences
                </Button>
            </Paper>

            {/* Subscriptions Section */}
            <Paper sx={{ padding: 4 }} elevation={3}>
                <Typography variant="h5" gutterBottom>
                    Subscriptions
                </Typography>
                <List>
                    {subscriptions.map((subscription) => (
                        <ListItem
                            key={subscription.id}
                            secondaryAction={
                                <Tooltip title="Remove Subscription">
                                    <IconButton
                                        edge="end"
                                        color="error"
                                        onClick={() => handleRemoveSubscription(subscription.id)}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </Tooltip>
                            }
                        >
                            <ListItemText primary={subscription.name} />
                        </ListItem>
                    ))}
                </List>
                <Box sx={{ display: "flex", alignItems: "center", marginTop: 2 }}>
                    <TextField
                        label="Add New Subscription"
                        value={newSubscription}
                        onChange={(e) => setNewSubscription(e.target.value)}
                        fullWidth
                    />
                    <Tooltip title="Add Subscription">
                        <IconButton
                            color="primary"
                            onClick={handleAddSubscription}
                            sx={{ marginLeft: 1 }}
                        >
                            <AddCircleOutlineIcon />
                        </IconButton>
                    </Tooltip>
                </Box>
            </Paper>
        </Box>
    );
};

export default Settings;
