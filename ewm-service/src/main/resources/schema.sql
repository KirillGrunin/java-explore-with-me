DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS participation_Request CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS compilations_events CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(250) NOT NULL,
                                     email VARCHAR(254) NOT NULL UNIQUE,
                                     CONSTRAINT pk_users PRIMARY KEY (id),
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS categories (
                                          id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                          name VARCHAR(50) NOT NULL,
                                          CONSTRAINT pk_categories PRIMARY KEY (id),
                                          CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name)
);
CREATE TABLE IF NOT EXISTS events (
                                      id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                      annotation VARCHAR(2000) NOT NULL,
                                      category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
                                      confirmed_Requests INTEGER NOT NULL,
                                      created_On TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                      description VARCHAR(7000) NOT NULL,
                                      event_Date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                      initiator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                      lat FLOAT,
                                      lon FLOAT,
                                      paid BOOLEAN NOT NULL,
                                      participant_Limit INTEGER,
                                      published_On TIMESTAMP WITHOUT TIME ZONE ,
                                      request_Moderation BOOLEAN ,
                                      state VARCHAR(255) NOT NULL,
                                      title VARCHAR(120) NOT NULL,
                                      CONSTRAINT pk_events PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS participation_Request (
                                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                                     created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                                     event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                                                     requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                                     status VARCHAR(255) NOT NULL,
                                                     CONSTRAINT pk_participation_Request PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS compilations (
                                            id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                            pinned BOOLEAN NOT NULL,
                                            title VARCHAR(50) NOT NULL,
                                            CONSTRAINT pk_compilations PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS compilations_events (
                                                   compilation_id BIGINT REFERENCES compilations(id) ON DELETE CASCADE,
                                                   event_id     BIGINT REFERENCES events(id) ON DELETE CASCADE,
                                                   PRIMARY KEY(compilation_id,event_id)
);
CREATE TABLE IF NOT EXISTS comments (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        text VARCHAR(512) NOT NULL,
                                        event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
                                        author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                        created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
                                        CONSTRAINT pk_comments PRIMARY KEY (id)
);