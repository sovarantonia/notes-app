import {useUser} from "./userContext";
import Sidebar from "./sidebar";
import React, {useEffect, useState} from "react";
import {filterNotesByTitle} from "./api";
import PaginatedTable from "./paginatedTable";
import { debounce } from 'lodash';
import "../resources/view-notes.css";

const ViewNotes = () => {
    const { logout } = useUser();
    const [notes, setNotes] = useState([]);
    const [error, setError] = useState('');
    const [title, setTitle] = useState('');

    const handleLogout = () => {
        logout();
    };

    const fetchNotes = async (titleValue) => {
        try {
            const data = await filterNotesByTitle(titleValue);
            setNotes(data);
        } catch (error) {
            setError('Error fetching notes:');
        }
    };

    const debouncedFetchNotes = debounce((titleValue) => {
        fetchNotes(titleValue);
    }, 300);

    useEffect(() => {
        fetchNotes('');
    }, []);

    const handleTitleChange = (e) => {
        const titleValue = e.target.value;
        setTitle(titleValue);
        debouncedFetchNotes(titleValue);
    };

    return (
        <div className="view-notes">
            <Sidebar onLogout={handleLogout} />
            <div className="main-content">
                <h2>View notes</h2>
                <input
                    type="search-text"
                    placeholder="Search..."
                    value={title}
                    onChange={handleTitleChange}
                />
                {error && <div className="error" aria-live="assertive">{error}</div>}
                <PaginatedTable data={notes} />
            </div>
        </div>
    );
};

export default ViewNotes;