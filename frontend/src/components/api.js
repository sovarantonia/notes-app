import axios from 'axios';
import fileDownload from 'js-file-download';

const API_URL = 'http://localhost:8080';

// Axios instance to automatically attach Authorization header if token is present
const api = axios.create({
    baseURL: API_URL,
});

// Interceptor to add the JWT token to the Authorization header on each request
api.interceptors.request.use((config) => {
    const token = sessionStorage.getItem('tokenValue');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Login API call
export const login = async (email, password) => {
    try {
        const response = await api.post('/login', {email, password});

        // Store the token in localStorage to persist session
        const {data} = response; // Destructure the response data
        return data;

    } catch (error) {
        console.error('Login error:', error);
        if (error.response && error.response.data) {
            throw new Error(error.response.data.message || 'Login failed');
        }
        throw new Error('Login failed');
    }
};

// Register API call
export const register = async (firstName, lastName, email, password) => {
    try {
        const response = await api.post('/register', {firstName, lastName, email, password});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Registration failed');
        }
    }
};

export const note = async (userId, title, text, date, grade) => {
    try {
        const response = await api.post('/notes', {userId, title, text, date, grade});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed creating a note');
        }
    }
}

export const updateUserCredentials = async (userId, firstName, lastName) => {
    try {
        const response = await api.patch(`user/${userId}`, {firstName, lastName});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed updating credentials');
        }
    }
}

export const deleteAccount = async (userId) => {
    try {
        const response = await api.delete(`user/${userId}`);
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed deleting account');
        }
    }
}

export const viewAllNotesByUser = async () => {
    try {
        const response = await api.get('/notes');
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving notes');
        }
    }
}

export const getNoteById = async (noteId) => {
    try {
        const response = await api.get(`/notes/${noteId}`);
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the note');
        }
    }
}

export const filterNotesByTitle = async (title) => {
    try {
        const response = await api.get('/notes/filter', {params: {string: title}});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the notes');
        }
    }
}

export const deleteNote = async (noteId) => {
    try {
        const response = await api.delete(`/notes/${noteId}`);
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed deleting the note');
        }
    }
}

export const updateNote = async (noteId, userId, title, text, date, grade) => {
    try {
        const response = await api.patch(`/notes/${noteId}`, {userId, title, text, grade, date});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed updating the note');
        }
    }
}

export const downloadNote = async (noteId, fileType) => {
    try {
        const response = await api.get(`/notes/${noteId}/download`, {
            params: {type: fileType},
            responseType: 'blob', // Ensure binary data (file)
        });

        const contentDisposition = response.headers['content-disposition'];

        const fileName = contentDisposition
            ? contentDisposition.split('filename=')[1].replace(/['"]/g, '')
            : `note.${fileType}`;

        fileDownload(response.data, fileName);

    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed downloading the note');
        }
    }
}

export const latestNotes = async () => {
    try {
        const response = await api.get('/notes/latest');
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the notes');
        }
    }
}

export const getNotesBetweenDates = async (startDate, endDate) => {
    try {
        const response = await api.get('/notes/dates', {params: {startDate: startDate, endDate: endDate}});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the notes');
        }
    }
}

export const sendRequest = async (senderId, receiverEmail) => {
    try {
        const response = await api.post('/requests', {senderId, receiverEmail});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed sending the request');
        }
    }
}

export const getReceivedRequests = async () => {
    try {
        const response = await api.get('/requests/received');
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the requests');
        }
    }
}

export const getSentRequests = async () => {
    try {
        const response = await api.get('/requests/sent');
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the requests');
        }
    }
}

export const acceptRequest = async (requestId) => {
    try {
        const response = await api.patch(`/requests/${requestId}/accept`);
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed accepting the request');
        }
    }
}

export const declineRequest = async (requestId) => {
    try {
        const response = await api.patch(`/requests/${requestId}/decline`);
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed declining the request');
        }
    }
}

export const getUserFriends = async () => {
    try {
        const response = await api.get('/user/friends');
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed getting friend list');
        }
    }
}

export const removeFriend = async (friendId) => {
    try {
        const response = await api.delete(`/requests/remove-friend/${friendId}`)
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed removing friend');
        }
    }
}

export const searchUsers = async (searchString) => {
    try {
        const response = await api.get('/user/search', {params: {searchString: searchString}});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the users');
        }
    }
}

export const searchUserFriends = async (searchString) => {
    try {
        const response = await api.get('/user/friends/search', {params: {searchString: searchString}});
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving user friends');
        }
    }
}

export const shareNote = async (noteId, receiverEmail) => {
    try {
        const response = await api.post(`/share/${noteId}`, receiverEmail, {
            headers: {
                'Content-Type': 'text/plain'
            }
        })
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed sharing note');
        }
    }
}

export const getShareById = async (noteId) => {
    try {
        const response = await api.get(`/share/${noteId}`)
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the share');
        }
    }
}

export const getSentSharedNotes = async (receiverEmail) => {
    try {
        const response = await api.get('/share/sent', {
            params: {receiverEmail: receiverEmail},
            headers: {
                'Content-Type': 'text/plain'
            }
        })
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the shares');
        }
    }
}

export const getReceivedSharedNotes = async (senderEmail) => {
    try {
        const response = await api.get('/share/sent', {
            params: {senderEmail: senderEmail},
            headers: {
                'Content-Type': 'text/plain'
            }
        })
        return response.data;
    } catch (error) {
        console.error('Error response:', error);
        if (error.response && error.response.data) {
            throw new Error(JSON.stringify(error.response.data));
        } else {
            throw new Error('Failed retrieving the shares');
        }
    }
}
