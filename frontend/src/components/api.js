import axios from 'axios';

const API_URL = 'http://localhost:8080';

export const login = async (email, password) => {
    try {
        const response = await axios.post(`${API_URL}/login`, {email, password});
        return response.data;
    } catch (error) {
            throw new Error('Login failed');
        }

};

export const register = async (firstName, lastName, email, password) => {
    try {
        const response = await axios.post(`${API_URL}/register`, {firstName, lastName, email, password});
        return response.data;
    } catch (error) {
        console.error('Error response:', error); // Log the entire error object
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Registration failed');
        }
    }
};


