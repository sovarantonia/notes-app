import './App.css';
import LandingPage from "./components/landingPage";
import LoginPage from "./components/loginPage";
import RegisterPage from "./components/registerPage";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import Header from "./components/header";

function App() {
    return (
        <div>
<Router>
    <Routes>
        <Route path="/" element={<LandingPage/>}/>
        <Route path="/login" element={<LoginPage/>}/>
        <Route path="/register" element={<RegisterPage/>}/>
    </Routes>
</Router>

        </div>
    );
}

export default App;
