import React, {useEffect, useState} from "react";
import {getUserFriends, removeFriend, searchUsers, sendRequest} from "./api";
import {debounce} from "lodash";
import Sidebar from "./sidebar";
import {useUser} from "./userContext";
import {
    Button,
    Dialog, DialogActions, DialogContent, DialogContentText,
    DialogTitle,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow
} from "@mui/material";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faTimes} from "@fortawesome/free-solid-svg-icons";
import '../resources/friends-page.css';
import DropdownInput from "./dropdownInput";

const FriendsPage = () => {
    const {logout} = useUser();
    const [error, setError] = useState('')
    const [friends, setFriends] = useState([])
    const [userList, setUserList] = useState([])
    const [selectedUser, setSelectedUser] = useState(null)
    const [openDialog, setOpenDialog] = useState(false);
    const [friendIdToRemove, setFriendIdToRemove] = useState(null);

    const handleLogout = () => {
        logout();
    };
    const sendFriendRequest = async (senderId, receiverEmail) => {
        try {
            await sendRequest(senderId, receiverEmail);
            alert("Request was sent")
        } catch (error) {
            setError("Error sending the request");
        }
    }

    const handleSendRequest = () => {
        if (selectedUser) {
            const user = JSON.parse(sessionStorage.getItem('userInfo')) || {};
            const senderId = user.id || null;
            const receiverEmail = selectedUser.email;
            sendFriendRequest(senderId, receiverEmail)
        } else {
            setError("Please select a user.");
        }
    }

    const fetchFriends = async () => {
        try {
            const data = await getUserFriends();
            setFriends(data);
        } catch (error) {
            setError('Error retrieving the friend list')
        }
    }

    const removeFromFriendList = async (friendId) => {
        try {
            await removeFriend(friendId);
            alert('User was removed from friend list')
            debounceFriendList();
        } catch (error) {
            setError('Error removing use from friend list')
        }
    }

    useEffect(() => {
        fetchFriends();
    }, []);

    const debounceFriendList = debounce(() => {
        fetchFriends()
    }, 300);

    const getUsers = async (searchString) => {
        const data = await searchUsers(searchString);
        setUserList(data);
    }

    const debounceGetUsers = debounce((searchString) => {
        getUsers(searchString);
    }, 300);

    const handleRemoveClick = (friendId) => {
        setFriendIdToRemove(friendId);
        setOpenDialog(true); // Open confirmation dialog
    };

    const confirmRemoveFriend = async () => {
        removeFromFriendList(friendIdToRemove)
        setOpenDialog(false); // Close dialog after action
        setFriendIdToRemove(null); // Reset friend ID
    };

    const handleCloseDialog = () => {
        setOpenDialog(false); // Close dialog without action
        setFriendIdToRemove(null); // Reset friend ID
    };


    return (
        <div className="main-content">
            <div className="friend-page">
                <Sidebar onLogout={handleLogout}/>
                {error && <div className="error" aria-live="assertive" id="errorMessage">{error}</div>}

                <h2>
                    Send request:<DropdownInput options={userList} onSearch={debounceGetUsers}
                                                onSelect={setSelectedUser}/>
                    <Button onClick={() => handleSendRequest()} id="sendRequestButton" className="send-button">Send</Button>
                </h2>

                <TableContainer id="friendList">
                    <Table sx={{width: '70%', marginTop: '50px', marginBottom: '20px', borderCollapse: 'collapse'}}
                           aria-label="friend-list">
                        <TableHead>
                            <TableRow>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>First name</TableCell>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Last name</TableCell>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Email</TableCell>

                                <TableCell sx={{
                                    backgroundColor: '#F2A2B1',
                                    fontWeight: 'bold',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>Option</TableCell>

                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {friends.map((item) => (
                                <TableRow
                                    key={item.id}
                                >
                                    <TableCell sx={{
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        whiteSpace: 'nowrap',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>{`${item.firstName}`}</TableCell>

                                    <TableCell sx={{
                                        overflow: 'hidden',
                                        textOverflow: 'ellipsis',
                                        whiteSpace: 'nowrap',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>{item.lastName}</TableCell>

                                    <TableCell
                                        sx={{
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{item.email}</TableCell>

                                    <TableCell sx={{padding: '12px', border: '1px solid #ddd'}}>
                                        <Button id="removeButton" size="2xl"
                                                onClick={() => handleRemoveClick(item.id)}><FontAwesomeIcon
                                            icon={faTimes}/>Remove</Button>
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>


                <Dialog open={openDialog} onClose={handleCloseDialog} className="delete-friend-dialog">
                    <DialogTitle>Confirm Removal</DialogTitle>
                    <DialogContent>
                        <DialogContentText>
                            This action cannot be undone
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <div className="dialog-buttons">
                            <Button onClick={confirmRemoveFriend} color="primary" variant="contained" id="deleteButton">
                                Remove
                            </Button>
                            <Button onClick={handleCloseDialog} color="secondary" variant="outlined" id="cancelButton">
                                Cancel
                            </Button>

                        </div>
                    </DialogActions>
                </Dialog>

            </div>
        </div>
    )
}
export default FriendsPage;