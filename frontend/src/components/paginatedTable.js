import React, {useState, useEffect} from 'react';
import {viewAllNotesByUser} from "./api";

const PaginatedTable = ({data}) => {
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10; // Number of items per page

    // Calculate the total number of pages
    const totalPages = Math.ceil(data.length / itemsPerPage);

    // Get the data for the current page
    const currentData = data.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    // Change the page
    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };


    return (
        <div>
            <table>
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Content</th>
                    <th>Date</th>
                    <th>Options</th>
                </tr>
                </thead>
                <tbody>
                {currentData.map(item => (
                    <tr key={item.id}>
                        <td>{item.title}</td>
                        <td>{item.text}</td>
                        <td>{item.date}</td>
                        <td>button</td>
                    </tr>
                ))}
                </tbody>
            </table>
            <div className="pagination">
                <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                >
                    Previous
                </button>
                <span>Page {currentPage} of {totalPages}</span>
                <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                >
                    Next
                </button>
            </div>
        </div>
    );
};

export default PaginatedTable;