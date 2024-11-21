import React from "react";
import { Snackbar, Alert } from "@mui/material";

const Notification = ({ message, severity, open, handleClose }) => {


    const handleSnackbarClose = (event, reason) => {
        if (reason === "clickaway") {
            return;
        }
        handleClose();
    };
    if (!open) {
        return null;
    }


    return (
        <Snackbar
            open={open}
            autoHideDuration={5000} // Notification will close automatically after 5 seconds
            onClose={handleClose}
            anchorOrigin={{ vertical: "top", horizontal: "center" }}
        >
            <Alert onClose={handleClose} severity={severity} sx={{ width: "100%" }}>
                {message}
            </Alert>
        </Snackbar>
    );
};

export default Notification;
