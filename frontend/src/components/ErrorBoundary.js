import React from 'react';
import {Button} from "@mui/material";
import {Component} from "react";

export default  class ErrorBoundary extends Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: '' };
    }

    static getDerivedStateFromError(error) {
        return { hasError: true, error };
    }

    componentDidCatch(error, errorInfo) {
        console.error("Error caught in ErrorBoundary:", error, errorInfo);
    }


    render() {
        if (this.state.hasError) {
            return (
                <div className="info">
                    <p>Oops! Something went wrong.</p>
                    <pre>{this.state.error && this.state.error.toString()}</pre>
                    <Button onClick={this.resetError}>Try again</Button>
                </div>
            );
        }

        return this.props.children;
    }
}



