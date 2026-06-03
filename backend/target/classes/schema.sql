-- =====================================================
-- KaamWala - PostgreSQL Database Schema
-- Hyperlocal Worker Marketplace
-- =====================================================

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- =====================================================
-- ENUM TYPES
-- =====================================================

CREATE TYPE user_role AS ENUM ('CUSTOMER', 'WORKER', 'ADMIN');
CREATE TYPE service_category AS ENUM (
    'CARPENTER', 'ELECTRICIAN', 'PLUMBER', 'PAINTER',
    'AC_TECHNICIAN', 'CCTV_INSTALLER', 'RO_SERVICE',
    'HOME_CLEANING', 'MASON', 'WELDER', 'FURNITURE_MAKER'
);
CREATE TYPE job_urgency AS ENUM ('NORMAL', 'URGENT', 'EMERGENCY');
CREATE TYPE job_status AS ENUM ('OPEN', 'BIDDING', 'BOOKED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED');
CREATE TYPE booking_status AS ENUM ('CONFIRMED', 'WORKER_EN_ROUTE', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'DISPUTED');
CREATE TYPE payment_status AS ENUM ('PENDING', 'AUTHORIZED', 'CAPTURED', 'REFUNDED');
CREATE TYPE subscription_tier AS ENUM ('FREE', 'PREMIUM');
CREATE TYPE availability_status AS ENUM ('AVAILABLE', 'BUSY', 'OFFLINE');
CREATE TYPE message_type AS ENUM ('TEXT', 'IMAGE', 'SYSTEM');
CREATE TYPE notification_type AS ENUM ('NEW_JOB', 'NEW_BID', 'BID_ACCEPTED', 'BOOKING_CONFIRMED', 'PAYMENT', 'REVIEW', 'SYSTEM');
CREATE TYPE verification_status AS ENUM ('PENDING', 'VERIFIED', 'REJECTED');

-- =====================================================
-- USERS TABLE
-- =====================================================

CREATE TABLE users (
    id              UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(100),
    phone           VARCHAR(15) NOT NULL UNIQUE,
    email           VARCHAR(150),
    avatar_url      VARCHAR(500),
    role            VARCHAR(20) NOT NULL,
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    fcm_token       VARCHAR(500),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_user_phone ON users (phone);
CREATE INDEX idx_user_role ON users (role);
CREATE INDEX idx_user_location ON users (latitude, longitude);

-- =====================================================
-- WORKER PROFILES TABLE
-- =====================================================

CREATE TABLE worker_profiles (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id             UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    starting_price      DECIMAL(10, 2),
    is_verified         BOOLEAN NOT NULL DEFAULT FALSE,
    aadhaar_verified    BOOLEAN NOT NULL DEFAULT FALSE,
    pan_verified        BOOLEAN NOT NULL DEFAULT FALSE,
    selfie_verified     BOOLEAN NOT NULL DEFAULT FALSE,
    rating_avg          DECIMAL(3, 2) DEFAULT 0.00,
    total_jobs          INTEGER NOT NULL DEFAULT 0,
    total_earnings      DECIMAL(12, 2) DEFAULT 0.00,
    bio                 VARCHAR(1000),
    subscription_tier   VARCHAR(20) NOT NULL DEFAULT 'FREE',
    availability_status VARCHAR(20) NOT NULL DEFAULT 'OFFLINE',
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_wp_user_id ON worker_profiles (user_id);
CREATE INDEX idx_wp_availability ON worker_profiles (availability_status);
CREATE INDEX idx_wp_verified ON worker_profiles (is_verified);

-- Worker skills (element collection)
CREATE TABLE worker_skills (
    worker_profile_id UUID NOT NULL REFERENCES worker_profiles(id) ON DELETE CASCADE,
    skill             VARCHAR(30) NOT NULL
);

CREATE INDEX idx_ws_profile ON worker_skills (worker_profile_id);
CREATE INDEX idx_ws_skill ON worker_skills (skill);

-- Worker service areas (element collection)
CREATE TABLE worker_service_areas (
    worker_profile_id UUID NOT NULL REFERENCES worker_profiles(id) ON DELETE CASCADE,
    area              VARCHAR(200) NOT NULL
);

CREATE INDEX idx_wsa_profile ON worker_service_areas (worker_profile_id);

-- =====================================================
-- JOB POSTS TABLE
-- =====================================================

CREATE TABLE job_posts (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    category    VARCHAR(30) NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    latitude    DOUBLE PRECISION NOT NULL,
    longitude   DOUBLE PRECISION NOT NULL,
    address     VARCHAR(500) NOT NULL,
    budget_min  DECIMAL(10, 2),
    budget_max  DECIMAL(10, 2),
    urgency     VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    status      VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at  TIMESTAMP
);

CREATE INDEX idx_job_status ON job_posts (status);
CREATE INDEX idx_job_category ON job_posts (category);
CREATE INDEX idx_job_customer ON job_posts (customer_id);
CREATE INDEX idx_job_location ON job_posts (latitude, longitude);
CREATE INDEX idx_job_created ON job_posts (created_at);
CREATE INDEX idx_job_status_category ON job_posts (status, category, created_at DESC);

-- Job photos (element collection)
CREATE TABLE job_photos (
    job_post_id UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    photo_url   VARCHAR(500) NOT NULL
);

CREATE INDEX idx_jp_job ON job_photos (job_post_id);

-- =====================================================
-- BIDS TABLE
-- =====================================================

CREATE TABLE bids (
    id                 UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_post_id        UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    worker_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    price              DECIMAL(10, 2) NOT NULL,
    message            VARCHAR(1000),
    estimated_duration INTEGER,
    is_accepted        BOOLEAN DEFAULT FALSE,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bid_job ON bids (job_post_id);
CREATE INDEX idx_bid_worker ON bids (worker_id);
CREATE INDEX idx_bid_created ON bids (created_at);
CREATE UNIQUE INDEX idx_bid_job_worker ON bids (job_post_id, worker_id);

-- =====================================================
-- BOOKINGS TABLE
-- =====================================================

CREATE TABLE bookings (
    id                UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    job_post_id       UUID NOT NULL REFERENCES job_posts(id) ON DELETE CASCADE,
    worker_id         UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    customer_id       UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    final_price       DECIMAL(10, 2) NOT NULL,
    status            VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    payment_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    otp_code          VARCHAR(6),
    worker_started_at TIMESTAMP,
    completed_at      TIMESTAMP,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_booking_worker ON bookings (worker_id);
CREATE INDEX idx_booking_customer ON bookings (customer_id);
CREATE INDEX idx_booking_status ON bookings (status);
CREATE INDEX idx_booking_created ON bookings (created_at);

-- =====================================================
-- REVIEWS TABLE
-- =====================================================

CREATE TABLE reviews (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id  UUID NOT NULL UNIQUE REFERENCES bookings(id) ON DELETE CASCADE,
    reviewer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    worker_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating      INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text VARCHAR(2000),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_review_booking ON reviews (booking_id);
CREATE INDEX idx_review_worker ON reviews (worker_id);

-- Review photos (element collection)
CREATE TABLE review_photos (
    review_id UUID NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    photo_url VARCHAR(500) NOT NULL
);

CREATE INDEX idx_rp_review ON review_photos (review_id);

-- =====================================================
-- CHAT MESSAGES TABLE
-- =====================================================

CREATE TABLE chat_messages (
    id           UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    booking_id   UUID NOT NULL,
    sender_id    UUID NOT NULL,
    receiver_id  UUID NOT NULL,
    message      VARCHAR(4000) NOT NULL,
    message_type VARCHAR(20) NOT NULL DEFAULT 'TEXT',
    is_read      BOOLEAN NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_chat_booking ON chat_messages (booking_id);
CREATE INDEX idx_chat_sender ON chat_messages (sender_id);
CREATE INDEX idx_chat_receiver ON chat_messages (receiver_id);
CREATE INDEX idx_chat_created ON chat_messages (created_at);
CREATE INDEX idx_chat_unread ON chat_messages (receiver_id, is_read) WHERE is_read = FALSE;

-- =====================================================
-- NOTIFICATIONS TABLE
-- =====================================================

CREATE TABLE notifications (
    id         UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(200) NOT NULL,
    body       VARCHAR(1000) NOT NULL,
    type       VARCHAR(30) NOT NULL,
    data       TEXT,
    is_read    BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notif_user ON notifications (user_id);
CREATE INDEX idx_notif_read ON notifications (is_read);
CREATE INDEX idx_notif_created ON notifications (created_at);
CREATE INDEX idx_notif_user_unread ON notifications (user_id, is_read) WHERE is_read = FALSE;

-- =====================================================
-- PORTFOLIO ITEMS TABLE
-- =====================================================

CREATE TABLE portfolio_items (
    id               UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    worker_id        UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title            VARCHAR(200) NOT NULL,
    description      VARCHAR(1000),
    before_image_url VARCHAR(500),
    after_image_url  VARCHAR(500),
    video_url        VARCHAR(500),
    category         VARCHAR(30) NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_portfolio_worker ON portfolio_items (worker_id);
CREATE INDEX idx_portfolio_category ON portfolio_items (category);

-- =====================================================
-- WORKER VERIFICATIONS TABLE
-- =====================================================

CREATE TABLE worker_verifications (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    worker_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    aadhaar_number      VARCHAR(500),  -- encrypted
    pan_number          VARCHAR(500),  -- encrypted
    selfie_url          VARCHAR(500),
    aadhaar_doc_url     VARCHAR(500),
    pan_doc_url         VARCHAR(500),
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_at         TIMESTAMP,
    verified_by         UUID,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_wv_worker ON worker_verifications (worker_id);
CREATE INDEX idx_wv_status ON worker_verifications (verification_status);

-- =====================================================
-- SPATIAL INDEX (using PostGIS for production)
-- =====================================================

-- Create a spatial function for nearby search using Haversine formula
-- This is used when PostGIS is not available or as a fallback
CREATE OR REPLACE FUNCTION haversine_distance(
    lat1 DOUBLE PRECISION,
    lon1 DOUBLE PRECISION,
    lat2 DOUBLE PRECISION,
    lon2 DOUBLE PRECISION
) RETURNS DOUBLE PRECISION AS $$
BEGIN
    RETURN 6371 * acos(
        cos(radians(lat1)) * cos(radians(lat2))
        * cos(radians(lon2) - radians(lon1))
        + sin(radians(lat1)) * sin(radians(lat2))
    );
END;
$$ LANGUAGE plpgsql IMMUTABLE;

-- =====================================================
-- TRIGGER: Auto-update updated_at timestamp
-- =====================================================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trigger_worker_profiles_updated_at
    BEFORE UPDATE ON worker_profiles
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
