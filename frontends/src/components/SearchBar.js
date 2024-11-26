import React, {useEffect, useState} from "react";
import {axiosPrivate} from "../api/axios";

const SearchBar = ({ value }) => {
    const [search,setSearch]=useState('')

    useEffect(() => {
        setSearch(value)
    }, [search]);

    function onSearch(e){

        e.preventDefault();
        setSearch(e.target.value);

     axiosPrivate.get(
            `/api/v3/users?search=${search}`,
            {
                headers: {
                    "Content-Type": "application/json",
                },
            }
        ).then((res) => {
            console.log(res.data);
           setSearch(res.data)
        })


    }


    return (
        <div style={{ marginBottom: "20px" }}>
            <input
                type="text"
                placeholder="Search..."
                onChange={(e) => onSearch(e.target.value)}
                style={{
                    padding: "10px",
                    width: "100%",
                    border: "1px solid #ddd",
                    borderRadius: "5px",
                    marginBottom: "10px",
                    fontSize: "16px",
                    outline: "none"
                }}
            />
            {search}



        </div>
    );
};

export default SearchBar;
