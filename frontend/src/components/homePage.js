import React, {useEffect, useState} from "react";
import Sidebar from "./sidebar";
import '../resources/homepage.css';
import {useUser} from "./userContext";
import {Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {latestNotes} from "./api";
import "../resources/table.css";
import NotesChart from "./notesChart";
const HomePage = () => {
    const { logout } = useUser();

    const handleLogout = () => {
        logout();
    };

    const [notes, setNotes] = useState([])
    const [error, setError] = useState('')

    const getLatestNotes = async () => {
        try {
            const response = await latestNotes();
            setNotes(response)
        }catch (error){
            setError('Error retrieving the notes')
        }
    }

    useEffect(() => {
        getLatestNotes();
    }, []);

    return (
        <div className="home-page">
            <Sidebar onLogout={handleLogout}/>
            <div className="main-content">
                <NotesChart/>
                {error && <div className="error" aria-live="assertive">{error}</div>}
                <h2>Latest notes</h2>
                <TableContainer>
                    <Table sx={{ width: '70%', margin: '20px 0', borderCollapse: 'collapse' }} aria-label="latest-notes">
                        <TableHead>
                            <TableRow>
                                <TableCell sx={{ backgroundColor: '#F2A2B1', fontWeight: 'bold', padding: '12px', border: '1px solid #ddd' }}>Title</TableCell>
                                <TableCell sx={{ backgroundColor: '#F2A2B1', fontWeight: 'bold', padding: '12px', border: '1px solid #ddd' }}>Content</TableCell>
                                <TableCell sx={{ backgroundColor: '#F2A2B1', fontWeight: 'bold', padding: '12px', border: '1px solid #ddd' }}>Date</TableCell>
                                <TableCell sx={{ backgroundColor: '#F2A2B1', fontWeight: 'bold', padding: '12px', border: '1px solid #ddd' }}>Grade</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {notes.map((item) => (
                                <TableRow
                                    key={item.id}
                                >
                                    <TableCell sx={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', padding: '12px', border: '1px solid #ddd' }}>{item.title}</TableCell>
                                    <TableCell sx={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', padding: '12px', border: '1px solid #ddd' }}>{item.text}</TableCell>
                                    <TableCell sx={{ padding: '12px', border: '1px solid #ddd' }}>{item.date}</TableCell>
                                    <TableCell sx={{ padding: '12px', border: '1px solid #ddd' }}>{item.grade}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>


            </div>
        </div>
    );
};

export default HomePage;