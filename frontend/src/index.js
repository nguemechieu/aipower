import "./index.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { createRoot } from "react-dom/client";
import {StrictMode, Suspense, lazy, Component} from "react";

import { AuthProvider } from "./context/AuthProvider.js";
import LoadingSpinner from "./components/LoadingSpinner.js";

// Lazy-load main application component for improved performance
const LazyApp = lazy(() => import("./App.js"));

// Error Boundary Component for graceful error handling as a class component
class ErrorBoundary extends Component {
    constructor(props) {
        super(props);
        this.state = { hasError: false, error: '' };
    }

    static getDerivedStateFromError(error) {
        // Update state so the next render shows the fallback UI
        return { hasError: true, error };
    }

    componentDidCatch(error, errorInfo) {
        // You can log the error or send it to a reporting service here
        console.error("Error caught in ErrorBoundary:", error, errorInfo);
    }

    resetError = () => {
        this.setState({ hasError: false, error: '' });
    };

    render() {
        if (this.state.hasError) {
            return (
                <div role="alert">
                    <p>Something went wrong</p>
                    <pre>{this.state.error && this.state.error.toString()}</pre>
                    <button onClick={this.resetError}>Try again</button>
                </div>
            );
        }

        return this.props.children;
    }
}

createRoot(document.getElementById("root")).render(
    <StrictMode>

        <ErrorBoundary>
        <BrowserRouter>
            <AuthProvider>

                        <Suspense fallback={
                            <LoadingSpinner />}>
                        <Routes>
                            <Route path="/*" element={<LazyApp />} />
                        </Routes>   </Suspense>

            </AuthProvider>
        </BrowserRouter>
        </ErrorBoundary>

    </StrictMode>
);
