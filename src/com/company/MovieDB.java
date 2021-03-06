/*
    Copyright 2017 RJ Russell

    Permission is hereby granted, free of charge, to any person obtaining a copy of
    this software and associated documentation files (the "Software"), to deal in
    the Software without restriction, including without limitation the rights to
    use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
    the Software, and to permit persons to whom the Software is furnished to do so,
    subject to the following conditions:

    The above copyright notice and this permission notice shall be included
    in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
    OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
    IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
    CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
    TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
    SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.company;

import java.sql.*;
import java.util.Arrays;

import static com.company.MovieDBConnection.DB_PASSWORD;
import static com.company.MovieDBConnection.DB_URL;
import static com.company.MovieDBConnection.DB_USERNAME;
import static java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;

/**
 * @author RJ Russell
 */

class MovieDB {
    private Connection conn;
    private Statement stmt;
    private PreparedStatement pstmt;

    /**
     * Default constructor. Connects to the database on creation of MovieDB object.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    MovieDB() throws SQLException, ClassNotFoundException {
        connectDatabase();
    }

    /**
     * Establishes a connection to the database.
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    private void connectDatabase() throws SQLException, ClassNotFoundException {
        final String JDBC_DRIVER = "org.h2.Driver";
        final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS `movies`"
                        + "(_id BIGINT NOT NULL AUTO_INCREMENT, "
                        + "`imdb_id` VARCHAR(255) UNIQUE, "
                        + "`title` VARCHAR(255), "
                        + "`year` VARCHAR(255), "
                        + "`content_rating` VARCHAR(255), "
                        + "`genres` VARCHAR(255), "
                        + "`actors` VARCHAR(255), "
                        + "`rating` VARCHAR(255), "
                        + "`runtime` VARCHAR(255), "
                        + "`plot` VARCHAR(255))";

        Class.forName(JDBC_DRIVER);
        conn = DriverManager.getConnection(
                DB_URL,
                DB_USERNAME,DB_PASSWORD);
        stmt = conn.createStatement();
        stmt.execute(CREATE_TABLE);
        System.out.println("DB Success\n\n");
    }

    /**
     * Inserts the data for a single movie into the database.
     * @param movie: A MovieData object that has all the data for one entry.
     * @throws SQLException
     */
    void insertMovie(MovieData movie) throws SQLException {
        final String insertStmt = "INSERT INTO `movies`(`_id`,`imdb_id`," +
                "`title`,`year`,`content_rating`,`genres`,`actors`,`rating`," +
                "`runtime`,`plot`) VALUES(DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        pstmt = conn.prepareStatement(insertStmt);
        pstmt.setString(1, movie.getImdbId());
        pstmt.setString(2, movie.getTitle());
        pstmt.setString(3, movie.getYear());
        pstmt.setString(4, movie.getContentRating());
        pstmt.setString(5, movie.getGenre());
        pstmt.setString(6, movie.getStars());
        pstmt.setString(7, movie.getRating());
        pstmt.setString(8, movie.getRuntime());
        pstmt.setString(9, movie.getPlot());

        pstmt.executeUpdate();
        pstmt.close();
    }

    /**
     * Removes a movie from database matching the given id.
     * @param id: The id for the movie created on insertion of movie.
     * @throws SQLException
     */
    void removeMovie(String id) throws SQLException {
        pstmt = conn.prepareStatement("DELETE FROM `movies` WHERE _ID = ?;");
        pstmt.setString(1, id);
        pstmt.executeUpdate();
        pstmt.close();
    }

    MovieData[] searchAll() throws SQLException {
        return searchQuery("SELECT * FROM `movies`");
    }


    MovieData[] search(String title, String year, String cr, String genre, String actors, String rating) throws SQLException {
        boolean first = true;

        String query = "SELECT * FROM `movies`";
        if(title.isEmpty() && year.isEmpty() && cr.isEmpty() && genre.isEmpty() && actors.isEmpty() && rating.isEmpty()) {
            return searchQuery(query + ";");
        } else {
            query += " WHERE ";
            if(!title.isEmpty()) {
                query += "`title` = '" + title + "'";
                first = false;
            }

            if(!year.isEmpty()) {
                if(!first) { query += " AND "; }
                query += " `year` = '" + year + "'";
                first = false;
            }

            if(!cr.isEmpty()) {
                if(!first) { query += " AND "; }
                query += " `content_rating` = '" + cr + "'";
                first = false;
            }

            if(!genre.isEmpty()) {
                if(!first) { query += " AND "; }
                query += " `genres` LIKE '%" + genre + "%'";
                first = false;
            }

            if(!actors.isEmpty()) {
                if(!first) { query += " AND "; }
                query += " `actors` LIKE '%" + actors + "%'";
                first = false;
            }

            if(!rating.isEmpty()) {
                if(!first) { query += " AND "; }
                query += " `rating = '" + rating + "'";
            }

            query += ";";
            return searchQuery(query);
        }
    }

    /**
     * Gathers the data for each movie entry in the database.
     * @return Array of MovieData objects.
     * @throws SQLException
     */
    private MovieData[] searchQuery(String query) throws SQLException {
        stmt = conn.createStatement(TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = stmt.executeQuery(query);

        int rows = 0;
        if(rs.last()) {
            rows = rs.getRow();
        }

        if(rows == 0) {
            return null;
        }
        // TODO: This is for debugging. Remove later.
        // System.out.println("ROWS: " + rows);

        MovieData[] movieResults = new MovieData[rows];

        // Reset cursor.
        rs.beforeFirst();
        int i = 0;
        while(rs.next()) {
            String id = rs.getString("_ID");
            String imdbId = rs.getString("IMDB_ID");
            String title = rs.getString("TITLE");
            String year = rs.getString("YEAR");
            String contentRating = rs.getString("CONTENT_RATING");

            String[] genre = rs.getString("GENRES").split(",");
            Arrays.stream(genre).map(String::trim).toArray(s -> genre);

            String[] stars = rs.getString("ACTORS").split(",");
            Arrays.stream(stars).map(String::trim).toArray(s -> stars);

            String rating = rs.getString("RATING");
            String runtime = rs.getString("RUNTIME");
            String plot = rs.getString("PLOT");

            movieResults[i] = new MovieData(id, imdbId, title, year,
                    contentRating, genre, stars, rating, runtime, plot);
            ++i;
        }
        return movieResults;
    }

    void update(MovieData movie) throws SQLException {
        final String stmt = "UPDATE movies SET `imdb_id`= ?, `title` = ?, `year` = ?," +
                "`content_rating` = ?, `genres` = ?, `actors` = ?, `rating` = ?, " +
                "`runtime` = ?, `plot` = ? WHERE `_ID` = ?";

        PreparedStatement pstmt = conn.prepareStatement(stmt);
        pstmt.setString(1, movie.getImdbId());
        pstmt.setString(2, movie.getTitle());
        pstmt.setString(3, movie.getYear());
        pstmt.setString(4, movie.getContentRating());
        pstmt.setString(5, movie.getGenre());
        pstmt.setString(6, movie.getStars());
        pstmt.setString(7, movie.getRating());
        pstmt.setString(8, movie.getRuntime());
        pstmt.setString(9, movie.getPlot());
        pstmt.setString(10, movie.getId());

        pstmt.executeUpdate();
        pstmt.close();
    }

}
