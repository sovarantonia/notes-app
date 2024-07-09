import './App.css';
import LandingPage from "./components/landingPage";
import LoginPage from "./components/loginPage";
import RegisterPage from "./components/registerPage";
import {BrowserRouter as Router, Navigate, Route, Routes} from "react-router-dom";
import HomePage from "./components/homePage";
import {useEffect, useState} from "react";
import {login} from "./components/api";

function App() {
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
