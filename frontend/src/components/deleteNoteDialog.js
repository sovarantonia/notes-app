import {Button, Dialog} from "@mui/material";
import React, {useState} from "react";
import {deleteNote} from "./api";
import '../resources/delete-note-dialog.css';
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faEraser, faTimes} from "@fortawesome/free-solid-svg-icons";

const DeleteNoteDialog = ({open, onClose, noteId, onUpdate}) => {
    const [error, setError] = useState('');

    const handleDeleteNote = async () => {
        try {
            await deleteNote(noteId);
            alert('Note was deleted')
            setError('')
            onUpdate();

        } catch (error) {
            setError('Error deleting the note');
        }
    }

    return (
        <Dialog open={open} onClose={onClose} className="delete-note-dialog">
            <div className="dialog-content">

                <h2>Delete Note</h2>
                {error && <div className="error" id="errorMessage">{error}</div>}
                <p>This action cannot be undone.</p>
                <div className="dialog-buttons">

                    <Button id="deleteNoteButton" onClick={handleDeleteNote} variant="contained" color="primary">
                        <FontAwesomeIcon icon={faEraser}/> Delete
                    </Button>

                    <Button id="cancelButton" onClick={onClose} variant="outlined" color="secondary">
                        <FontAwesomeIcon icon={faTimes}/> Cancel
                    </Button>
                </div>
            </div>

        </Dialog>
    );
};

export default DeleteNoteDialog;

