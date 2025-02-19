import React, { useEffect, useState } from "react";
import { axiosPrivate } from "../api/axios";

const SearchBar = ({ value }: never) => {
  const [search, setSearch] = useState("");
  const onSearchChange = (values: React.SetStateAction<string>) => {
    setSearch(values);
  };

  useEffect(() => {
    setSearch(search);
    onSearch(search);
  }, [search]);

  function onSearch(event: string) {
    axiosPrivate
      .get(`/api/v3/users?search=${search}`, {
        headers: {
          "Content-Type": "application/json",
        },
      })
      .then((res) => {
        console.log(res.data);
        setSearch(res.data);
      });
  }

  return (
    <div style={{ marginBottom: "20px" }}>
      <input
        type="text"
        id="search"
        value={search}
        placeholder="Search..."
        onChange={(e) => onSearchChange(e.target.value)}
        style={{
          padding: "10px",
          width: "100%",
          border: "1px solid #ddd",
          borderRadius: "5px",
          marginBottom: "10px",
          fontSize: "16px",
          outline: "none",
        }}
      />
      {search}
    </div>
  );
};

export default SearchBar;
