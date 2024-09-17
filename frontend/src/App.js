import './App.css';
import LandingPage from "./components/landingPage";
import LoginPage from "./components/loginPage";
import RegisterPage from "./components/registerPage";
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";
import HomePage from "./components/homePage";
import CreateNotePage from "./components/createNotePage";
import {UserProvider} from "./components/userContext";
import ProtectedRoute from "./components/protectedRoute";
import UserProfile from "./components/userProfile";
import ViewNotes from "./components/viewNotes";

function App() {
    return (
        <UserProvider>
            <Router>
                <Routes>
                    <Route path="/" element={<LandingPage/>}/>
                    <Route path="/login" element={<LoginPage/>}/>
                    <Route path="/register" element={<RegisterPage/>}/>
                    <Route
                        path="/home"
                        element={<ProtectedRoute element={HomePage}/>}
                    />
                    <Route
                        path="/create-note"
                        element={<ProtectedRoute element={CreateNotePage}/>}
                    />
                    <Route
                        path="/profile"
                        element={<ProtectedRoute element={UserProfile}/>}
                    />
                    <Route
                        path="/view-notes"
                        element={<ProtectedRoute element={ViewNotes}/>}
                    />
                </Routes>
            </Router>
        </UserProvider>
    );
}

export default App;
