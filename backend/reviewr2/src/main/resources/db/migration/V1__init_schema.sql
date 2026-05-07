CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER', -- USER, MOD, RECRUITER

    -- Gamification & Profile
    credibility_score INT DEFAULT 0,
    rank_tier VARCHAR(20) DEFAULT 'NOVICE', -- NOVICE, BRONZE, SILVER, GOLD, DIAMOND
    open_to_work BOOLEAN DEFAULT FALSE,

    -- Moderation
    strikes INT DEFAULT 0,
    is_shadowbanned BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Fuzzy search index for the debounced user search
CREATE INDEX idx_users_username_trgm ON users USING GIN (username gin_trgm_ops);

CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL,
    post_type VARCHAR(20) NOT NULL, -- SNIPPET or QUESTION
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    code TEXT, -- Nullable because Questions might not have code
    language VARCHAR(50),
    status VARCHAR(20) DEFAULT 'ACTIVE', -- ACTIVE, PENDING_APPROVAL (for shadowbanned)

    -- Full Text Search Vector
    search_vector tsvector GENERATED ALWAYS AS (
        setweight(to_tsvector('english', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('english', coalesce(content, '')), 'B')
    ) STORED,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_post_author FOREIGN KEY (author_id)
        REFERENCES users(id) ON DELETE CASCADE
);

-- GIN Index for blazing fast lexical search on posts
CREATE INDEX idx_posts_search ON posts USING GIN (search_vector);

CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE post_tags (
    post_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    CONSTRAINT fk_pt_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_pt_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);


CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    parent_comment_id BIGINT, -- NULL means it's a top-level comment
    content TEXT NOT NULL,
    is_accepted_solution BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_comment_post FOREIGN KEY (post_id)
        REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_author FOREIGN KEY (author_id)
        REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_comment_parent FOREIGN KEY (parent_comment_id)
        REFERENCES comments(id) ON DELETE CASCADE
);

-- Index for fetching a post's comments quickly
CREATE INDEX idx_comments_post_id ON comments(post_id);


CREATE TABLE post_votes (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (1, -1)),
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_pv_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_pv_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

CREATE TABLE comment_votes (
    user_id BIGINT NOT NULL,
    comment_id BIGINT NOT NULL,
    vote_value SMALLINT NOT NULL CHECK (vote_value IN (1, -1)),
    PRIMARY KEY (user_id, comment_id),
    CONSTRAINT fk_cv_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cv_comment FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE
);

CREATE TABLE bookmarks (
    user_id BIGINT NOT NULL,
    post_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, post_id),
    CONSTRAINT fk_bm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_bm_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);