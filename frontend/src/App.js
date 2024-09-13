import './App.css';
import LandingPage from "./components/landingPage";
import LoginPage from "./components/loginPage";
import RegisterPage from "./components/registerPage";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import HomePage from "./components/homePage";
import {useEffect, useState} from "react";
import {login} from "./components/api";
import CreateNotePage from "./components/createNotePage";
import {UserProvider} from "./components/userContext";

function App() {
    // const [loggedIn, setLoggedIn] = useState(false);
    //
    // // Check if user is logged in on mount
    // useEffect(() => {
    //     const token = localStorage.getItem('tokenValue');
    //     if (token) {
    //         setLoggedIn(true); // User is logged in if token exists
    //     }
    // }, []);
    //
    // const handleLogin = async (email, password) => {
    //     try {
    //         await login(email, password);
    //         setLoggedIn(true); // Set user as logged in after successful login
    //     } catch (error) {
    //         console.error('Login failed', error);
    //     }
    // };
    //
    // const handleLogout = () => {
    //     localStorage.removeItem('tokenValue'); // Remove the token on logout
    //     setLoggedIn(false); // Set logged in state to false
    // };
    return (
        <UserProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route path="/home" element={<HomePage />} />
                    <Route path="/create-note" element={<CreateNotePage />} />
                </Routes>
            </Router>
        </UserProvider>
    );
}

export default App;
