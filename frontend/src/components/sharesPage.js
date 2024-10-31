import {useUser} from "./userContext";
import React, {useEffect, useState} from "react";
import Sidebar from "./sidebar";
import {getReceivedSharedNotes, getSentSharedNotes, getUserFriends} from "./api";
import {debounce} from "lodash";
import {Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from "@mui/material";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faPenToSquare} from "@fortawesome/free-solid-svg-icons";
import DropdownInput from "./dropdownInput";
import SharedNoteDialog from "./viewSharedNoteDialog";

const SharesPage = () => {
    const {logout} = useUser();
    const [error, setError] = useState('');
    const [sentShares, setSentShares] = useState([])
    const [receivedShares, setReceivedShares] = useState([])
    const [showTable, setShowTable] = useState(false);
    const [userFriendList, setUserFriendList] = useState([])
    const [selectedUser, setSelectedUser] = useState(null)

    const [open, setOpen] = useState(false);
    const [selectedNoteId, setSelectedNoteId] = useState(null);
    const [activeDialog, setActiveDialog] = useState(null);

    const handleLogout = () => {
        logout();
    };

    const fetchSentShares = async (receiverEmail) => {
        try {
            const data = await getSentSharedNotes(receiverEmail)
            setSentShares(data)
        } catch (error) {
            setError('Error retrieving the shared notes')
        }
    }

    const debounceSentShares = debounce((receiverEmail) => {
        fetchSentShares(receiverEmail)
    }, 300)

    const fetchReceivedShares = async (senderEmail) => {
        try {
            const data = await getReceivedSharedNotes(senderEmail)
            setReceivedShares(data)
            setError('')
        } catch (error) {
            setError('Error retrieving the shared notes')
        }
    }

    const debounceReceivedShares = debounce((senderEmail) => {
        fetchReceivedShares(senderEmail)
    }, 300)


    useEffect(() => {
        fetchSentShares('')
        fetchReceivedShares('')
        fetchUserFriends()
    }, []);

    const fetchUserFriends = async () => {
        try {
            const data = await getUserFriends();
            setUserFriendList(data)
            setError('')
        } catch (error) {
            setError('Error retrieving the friends')
        }
    }

    const toggleTable = () => {
        setShowTable(prevShowTable => !prevShowTable);
    };

    const handleOpenDialog = (noteId, dialogType) => {
        setSelectedNoteId(noteId);
        setOpen(true);
        setActiveDialog(dialogType);
    };

    const handleCloseDialog = () => {
        setOpen(false);
        setActiveDialog(null);
    };

    return (
        <div className="main-content">
            <Sidebar onLogout={handleLogout}/>
            {error && <div className="error" aria-live="assertive" id="errorMessage">{error}</div>}

            <DropdownInput options={userFriendList} onSearch={debounceSentShares}
                           onSelect={(user) => {
                               setSelectedUser(user); // Store the full user object
                               debounceSentShares(user.email); // Pass the email to debounce function
                           }}/>

            <TableContainer id="sentSharedNotes">
                <Table sx={{width: '70%', margin: '20px 0', borderCollapse: 'collapse'}}
                       aria-label="sent-shared-notes">
                    <TableHead>
                        <TableRow>

                            <TableCell sx={{
                                backgroundColor: '#F2A2B1',
                                fontWeight: 'bold',
                                padding: '12px',
                                border: '1px solid #ddd'
                            }}>Receiver</TableCell>

                            <TableCell sx={{
                                backgroundColor: '#F2A2B1',
                                fontWeight: 'bold',
                                padding: '12px',
                                border: '1px solid #ddd'
                            }}>Note title</TableCell>

                            <TableCell sx={{
                                backgroundColor: '#F2A2B1',
                                fontWeight: 'bold',
                                padding: '12px',
                                border: '1px solid #ddd'
                            }}>Note content</TableCell>

                            <TableCell sx={{
                                backgroundColor: '#F2A2B1',
                                fontWeight: 'bold',
                                padding: '12px',
                                border: '1px solid #ddd'
                            }}>Created at</TableCell>

                            <TableCell sx={{
                                backgroundColor: '#F2A2B1',
                                fontWeight: 'bold',
                                padding: '12px',
                                border: '1px solid #ddd'
                            }}>Sent at</TableCell>

                            <TableCell sx={{
                                backgroundColor: '#F2A2B1',
                                fontWeight: 'bold',
                                padding: '12px',
                                border: '1px solid #ddd'
                            }}>Options</TableCell>

                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {sentShares.map((item) => (
                            <TableRow
                                key={item.id}
                            >
                                <TableCell sx={{
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                    whiteSpace: 'nowrap',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>{`${item.receiver.firstName} ${item.receiver.lastName} (${item.receiver.email})`}</TableCell>

                                <TableCell sx={{
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                    whiteSpace: 'nowrap',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>{item.sentNote.title}</TableCell>

                                <TableCell sx={{
                                    overflow: 'hidden',
                                    textOverflow: 'ellipsis',
                                    whiteSpace: 'nowrap',
                                    padding: '12px',
                                    border: '1px solid #ddd'
                                }}>{item.sentNote.text}</TableCell>

                                <TableCell
                                    sx={{padding: '12px', border: '1px solid #ddd'}}>{item.sentNote.date}</TableCell>

                                <TableCell
                                    sx={{padding: '12px', border: '1px solid #ddd'}}>{item.sentAt}</TableCell>

                                <TableCell sx={{padding: '12px', border: '1px solid #ddd'}}>
                                    <Button id="viewNoteButton" size="2xl"
                                            onClick={() => handleOpenDialog(item.sentNote.id, "view-received-note")}
                                            title="View note" sx={{
                                        backgroundColor: 'transparent',
                                        color: '#48494c',
                                    }}><FontAwesomeIcon
                                        icon={faPenToSquare}/></Button>

                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <div className="dialog-buttons">
                <Button id="sentRequestTableButton" onClick={toggleTable} variant="contained">
                    {showTable ? 'Hide received notes' : 'Show received notes'}
                </Button>
            </div>

            {showTable && (
                <>
                    <DropdownInput options={userFriendList} onSearch={debounceReceivedShares}
                                   onSelect={(user) => {
                                       setSelectedUser(user); // Store the full user object
                                       debounceReceivedShares(user.email); // Pass the email to debounce function
                                   }}/>
                    <TableContainer id="receivedSharedNotes">
                        <Table sx={{width: '70%', margin: '20px 0', borderCollapse: 'collapse'}}
                               aria-label="received-shared-notes">
                            <TableHead>
                                <TableRow>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Sender</TableCell>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Note title</TableCell>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Note content</TableCell>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Note grade</TableCell>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Created at</TableCell>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Sent at</TableCell>
                                    <TableCell sx={{
                                        backgroundColor: '#F2A2B1',
                                        fontWeight: 'bold',
                                        padding: '12px',
                                        border: '1px solid #ddd'
                                    }}>Options</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {receivedShares.map((item) => (
                                    <TableRow key={item.id}>
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{`${item.sender.firstName} ${item.sender.lastName} (${item.sender.email})`}</TableCell>
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{item.sentNote.title}</TableCell>
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{item.sentNote.text}</TableCell>
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{item.sentNote.grade}</TableCell>
                                        <TableCell sx={{
                                            overflow: 'hidden',
                                            textOverflow: 'ellipsis',
                                            whiteSpace: 'nowrap',
                                            padding: '12px',
                                            border: '1px solid #ddd'
                                        }}>{item.sentNote.date}</TableCell>
                                        <TableCell
                                            sx={{padding: '12px', border: '1px solid #ddd'}}>{item.sentAt}</TableCell>
                                        <TableCell sx={{padding: '12px', border: '1px solid #ddd'}}>
                                            <Button id="viewNoteButton" size="2xl"
                                                    onClick={() => handleOpenDialog(item.sentNote.id, "view-sent-note")}
                                                    title="View note" sx={{
                                                backgroundColor: 'transparent',
                                                color: '#48494c',
                                            }}>
                                                <FontAwesomeIcon icon={faPenToSquare}/>
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </>
            )}

            {selectedNoteId !== null && (
                <SharedNoteDialog
                    open={open}
                    onClose={handleCloseDialog}
                    noteId={selectedNoteId}
                    dialogType={activeDialog}
                />
            )}
        </div>
    )
}

export default SharesPage;