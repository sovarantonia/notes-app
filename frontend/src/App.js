import './App.css';
import LandingPage from "./components/landingPage";
import LoginPage from "./components/loginPage";
import RegisterPage from "./components/registerPage";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import HomePage from "./components/homePage";
import {useEffect, useState} from "react";
import {login} from "./components/api";

function App() {
    const [loggedIn, setLoggedIn] = useState(false);
    const [userData, setUserData] = useState(null);

    const handleLogin = async (email, password) => {
        try {
            const data = await login(email, password);
            setLoggedIn(true);
            setUserData(data); // Store user data (e.g., token) here
            localStorage.setItem('tokenValue', data.token); // Store token in local storage for persistence
        } catch (error) {
            setLoggedIn(false);
            setUserData(null);
            throw new Error();
        }
    };

    const handleLogout = () => {
        setLoggedIn(false);
        setUserData(null);
        localStorage.removeItem('tokenValue'); // Clear token from local storage
    };

    useEffect(() => {
        const token = localStorage.getItem('tokenValue');
        if (token) {
            setLoggedIn(true);
        }
    }, []);

    return (
        <div>
<Router>
    <Routes>
        <Route path="/" element={<LandingPage/>}/>
        <Route path="/login" element={<LoginPage onLogin={handleLogin}/>}/>
        <Route path="/register" element={<RegisterPage/>}/>
        <Route path="/home" element={loggedIn ? <HomePage onLogout={handleLogout} /> : <Navigate to="/login" replace />} />
    </Routes>
</Router>

        </div>
    );
}

export default App;
