import React, {useState} from 'react';
import Header from "./header";
import {register} from "./api";
import {redirect} from 'react-router-dom';

const RegisterPage = () => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState('');
    //const history = History.();

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            setError('Passwords do not match');
            return;
        }
        if (!validateEmail(email)) {
            setError('Invalid email format');
            return;
        }
        if (password.length < 6) {
            setError('Password must be at least 6 characters long');
            return;
        }

        try {
            await register(firstName, lastName, email, password);
            // Registration successful, display notification and redirect to login page
            alert('Registration successful! Please login with your credentials.');
            redirect('/login'); // Redirect to the login page
        } catch (error) {
            setError(error);
        }
    };

    const validateEmail = (email) => {
        // Basic email format validation
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    };

    return (
        <div>
            <Header/>
            <h2>Register</h2>
            {error && <div>{error}</div>}
            <form onSubmit={handleSubmit}>
                <input type="text" value={firstName} onChange={(e) => setFirstName(e.target.value)}
                       placeholder="First Name" required/>
                <input type="text" value={lastName} onChange={(e) => setLastName(e.target.value)}
                       placeholder="Last Name" required/>
                <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="Email"
                       required/>
                <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                       placeholder="Password" required/>
                <input type="password" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)}
                       placeholder="Confirm Password" required/>
                <button type="submit">Register</button>
            </form>
        </div>
    );
}

export default RegisterPage;