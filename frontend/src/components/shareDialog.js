import React, {useState} from "react";
import {searchUserFriends, shareNote} from "./api";
import {debounce} from "lodash";
import DropdownInput from "./dropdownInput";
import {Button, Dialog} from "@mui/material";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faArrowUpRightFromSquare, faTimes} from "@fortawesome/free-solid-svg-icons";
import '../resources/share-dialog.css';

const ShareDialog = ({open, onClose, noteId}) => {
    const [friends, setFriends] = useState([]);
    const [error, setError] = useState('');
    const [selectedUser, setSelectedUser] = useState(null);
    const getFriends = async (searchString) => {
        try {
            const data = await searchUserFriends(searchString);
            setFriends(data);
        } catch (error) {
            setError('Error retrieving the friend list')
        }
    }

    const debounceFriends = debounce((searchString) => {
        getFriends(searchString)
    }, 300);

    const shareNoteToFriend = async (noteId, receiverEmail) => {
        try {
            await shareNote(noteId, receiverEmail)
        } catch (error) {
            setError('Error sharing the note')
        }
    }

    const handleShareNote = (noteId) => {
        if (selectedUser) {
            const receiverEmail = selectedUser.email;
            shareNoteToFriend(noteId, receiverEmail)
            setError('')
            alert("Note was shared")
            onClose();
        } else {
            setError('Select a friend')
        }
    }
    return (
        <div className="dialog-content">
            <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm" className="share-dialog">
                {error && <div className="error" aria-live="assertive" id="errorMessage">{error}</div>}
                <h2>Share note</h2>
                <p>
                    Send to: <DropdownInput options={friends} onSearch={debounceFriends} onSelect={setSelectedUser}/>
                </p>
                <div className="dialog-buttons"
                     style={{display: 'flex', justifyContent: 'flex-end'}}>
                    <Button id="shareButton" onClick={() => handleShareNote(noteId)} variant="contained">
                        <FontAwesomeIcon icon={faArrowUpRightFromSquare}/>Send
                    </Button>
                    <Button id="cancelButton" onClick={onClose} variant="outlined" color="secondary">
                        <FontAwesomeIcon icon={faTimes}/> Cancel
                    </Button>
                </div>
            </Dialog>
        </div>
    )
}
export default ShareDialog;