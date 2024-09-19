import {Button, Dialog} from "@mui/material";
import React, {useState} from "react";
import {deleteNote} from "./api";

const DeleteNoteDialog = ({open, onClose, noteId, onUpdate}) => {
    const [error, setError] = useState('');

    const handleDeleteNote = async () => {
        try {
            await deleteNote(noteId);
            alert('Note was deleted')
            setError('')
            onUpdate();

        }catch (error) {
            setError('Error deleting the note');
        }
    }

    return (
        <Dialog open={open} onClose={onClose}>
            <h2>Delete Note</h2>
            {error && <div className="error">{error}</div>}
            <p>Are you sure you want to delete your note? This action cannot be undone.</p>
            <Button onClick={handleDeleteNote}>
                Delete
            </Button>
            <Button onClick={onClose}>
                Cancel
            </Button>
        </Dialog>
    );
};

export default DeleteNoteDialog;

