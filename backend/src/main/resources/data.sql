-- ====================================================================
-- KaamWala Mock Data Seed Script
-- Idempotent insertions for 10 workers in Kanpur and Delhi NCR
-- ====================================================================

-- 1. CLEANUP MOCK RECORDS TO ENABLE RE-RUNS
DELETE FROM worker_skills WHERE worker_profile_id IN (
    '20000000-0000-0000-0000-000000000001',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000003',
    '20000000-0000-0000-0000-000000000004',
    '20000000-0000-0000-0000-000000000005',
    '20000000-0000-0000-0000-000000000006',
    '20000000-0000-0000-0000-000000000007',
    '20000000-0000-0000-0000-000000000008',
    '20000000-0000-0000-0000-000000000009',
    '20000000-0000-0000-0000-000000000010'
);

DELETE FROM worker_service_areas WHERE worker_profile_id IN (
    '20000000-0000-0000-0000-000000000001',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000003',
    '20000000-0000-0000-0000-000000000004',
    '20000000-0000-0000-0000-000000000005',
    '20000000-0000-0000-0000-000000000006',
    '20000000-0000-0000-0000-000000000007',
    '20000000-0000-0000-0000-000000000008',
    '20000000-0000-0000-0000-000000000009',
    '20000000-0000-0000-0000-000000000010'
);

DELETE FROM portfolio_items WHERE worker_id IN (
    '10000000-0000-0000-0000-000000000001',
    '10000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000004',
    '10000000-0000-0000-0000-000000000005',
    '10000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000007',
    '10000000-0000-0000-0000-000000000008',
    '10000000-0000-0000-0000-000000000009',
    '10000000-0000-0000-0000-000000000010'
);

DELETE FROM worker_profiles WHERE id IN (
    '20000000-0000-0000-0000-000000000001',
    '20000000-0000-0000-0000-000000000002',
    '20000000-0000-0000-0000-000000000003',
    '20000000-0000-0000-0000-000000000004',
    '20000000-0000-0000-0000-000000000005',
    '20000000-0000-0000-0000-000000000006',
    '20000000-0000-0000-0000-000000000007',
    '20000000-0000-0000-0000-000000000008',
    '20000000-0000-0000-0000-000000000009',
    '20000000-0000-0000-0000-000000000010'
);

DELETE FROM users WHERE id IN (
    '10000000-0000-0000-0000-000000000001',
    '10000000-0000-0000-0000-000000000002',
    '10000000-0000-0000-0000-000000000003',
    '10000000-0000-0000-0000-000000000004',
    '10000000-0000-0000-0000-000000000005',
    '10000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000007',
    '10000000-0000-0000-0000-000000000008',
    '10000000-0000-0000-0000-000000000009',
    '10000000-0000-0000-0000-000000000010'
);

-- 2. INSERT USERS (Role: WORKER)
-- Swaroop Nagar, Kalyanpur, Govind Nagar, Kakadeo in Kanpur (lat ~26.45, lng ~80.33)
-- Saket, Noida, Gurgaon, Connaught Place, Karol Bagh in Delhi NCR (lat ~28.70, lng ~77.10)
INSERT INTO users (id, name, phone, email, avatar_url, role, latitude, longitude, is_active) VALUES
('10000000-0000-0000-0000-000000000001', 'Ramesh Kumar', '+919876543210', 'ramesh.kumar@example.com', 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=150', 'WORKER', 26.4744, 80.3168, true),
('10000000-0000-0000-0000-000000000002', 'Suresh Singh', '+919876543211', 'suresh.singh@example.com', 'https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=150', 'WORKER', 26.5126, 80.2452, true),
('10000000-0000-0000-0000-000000000003', 'Amit Gupta', '+919876543212', 'amit.gupta@example.com', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 'WORKER', 26.4764, 80.2974, true),
('10000000-0000-0000-0000-000000000004', 'Vikash Yadav', '+919876543213', 'vikash.yadav@example.com', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', 'WORKER', 26.4402, 80.3204, true),
('10000000-0000-0000-0000-000000000005', 'Rajesh Verma', '+919876543214', 'rajesh.verma@example.com', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150', 'WORKER', 26.4800, 80.3350, true),
('10000000-0000-0000-0000-000000000006', 'Anil Sharma', '+919876543215', 'anil.sharma@example.com', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', 'WORKER', 28.5244, 77.2066, true),
('10000000-0000-0000-0000-000000000007', 'Sunil Patel', '+919876543216', 'sunil.patel@example.com', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150', 'WORKER', 28.6273, 77.3725, true),
('10000000-0000-0000-0000-000000000008', 'Deepak Mishra', '+919876543217', 'deepak.mishra@example.com', 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150', 'WORKER', 28.4900, 77.0800, true),
('10000000-0000-0000-0000-000000000009', 'Karan Johar', '+919876543218', 'karan.johar@example.com', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150', 'WORKER', 28.6304, 77.2177, true),
('10000000-0000-0000-0000-000000000010', 'Manish Malhotra', '+919876543219', 'manish.malhotra@example.com', 'https://images.unsplash.com/photo-1489980508314-941910ded1f4?w=150', 'WORKER', 28.6500, 77.1900, true);

-- 3. INSERT WORKER PROFILES
INSERT INTO worker_profiles (id, user_id, starting_price, is_verified, aadhaar_verified, pan_verified, selfie_verified, rating_avg, total_jobs, total_earnings, bio, subscription_tier, availability_status) VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 300.00, true, true, true, true, 4.85, 42, 12600.00, 'Experienced carpenter specializing in modular kitchens, wardrobes, and wooden door repairs. 10+ years in the trade.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 250.00, true, true, false, true, 4.50, 18, 4500.00, 'Professional plumber handling kitchen leak repairs, toilet fittings, blockages, and water motor setup.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 180.00, true, true, true, true, 4.70, 75, 13500.00, 'Licensed electrician for house wiring, inverter setups, smart switches, and general electrical maintenance.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 500.00, false, false, false, false, 4.20, 10, 5000.00, 'Vibrant interior and exterior house painter. Expertise in wall putty, texture designs, and wood polishing.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000005', 400.00, true, true, true, true, 4.90, 110, 44000.00, 'AC mechanic expert in gas refilling, split & window AC installation, wet washing, and compressor repairs.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000006', 450.00, true, true, true, true, 4.75, 54, 24300.00, 'High-end modular furniture creator and structural woodwork designer serving Delhi & NCR.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000007', 350.00, true, true, false, true, 4.60, 32, 11200.00, 'Certified plumber for modern bathroom renovations, leak detection, and pipeline installations.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000008', 300.00, true, true, true, true, 4.80, 89, 26700.00, 'Expert electrician for commercial and residential installations, circuit board wiring, and lighting systems.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000009', 700.00, false, false, false, false, 4.40, 15, 10500.00, 'Professional painter for luxury apartments. Wall art, metallic textures, and premium waterproof coating.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000010', 600.00, true, true, true, true, 4.95, 140, 84000.00, 'Highly-rated heating and cooling expert. 12+ years installing HVAC systems and central air conditioning.', 'PREMIUM', 'AVAILABLE');

-- 4. INSERT WORKER SKILLS
INSERT INTO worker_skills (worker_profile_id, skill) VALUES
('20000000-0000-0000-0000-000000000001', 'CARPENTER'),
('20000000-0000-0000-0000-000000000001', 'FURNITURE_MAKER'),
('20000000-0000-0000-0000-000000000002', 'PLUMBER'),
('20000000-0000-0000-0000-000000000003', 'ELECTRICIAN'),
('20000000-0000-0000-0000-000000000004', 'PAINTER'),
('20000000-0000-0000-0000-000000000005', 'AC_TECHNICIAN'),
('20000000-0000-0000-0000-000000000006', 'CARPENTER'),
('20000000-0000-0000-0000-000000000006', 'FURNITURE_MAKER'),
('20000000-0000-0000-0000-000000000007', 'PLUMBER'),
('20000000-0000-0000-0000-000000000008', 'ELECTRICIAN'),
('20000000-0000-0000-0000-000000000009', 'PAINTER'),
('20000000-0000-0000-0000-000000000010', 'AC_TECHNICIAN');

-- 5. INSERT WORKER SERVICE AREAS
INSERT INTO worker_service_areas (worker_profile_id, area) VALUES
('20000000-0000-0000-0000-000000000001', 'Kanpur'),
('20000000-0000-0000-0000-000000000001', 'Swaroop Nagar'),
('20000000-0000-0000-0000-000000000001', 'Kakadeo'),
('20000000-0000-0000-0000-000000000002', 'Kanpur'),
('20000000-0000-0000-0000-000000000002', 'Kalyanpur'),
('20000000-0000-0000-0000-000000000003', 'Kanpur'),
('20000000-0000-0000-0000-000000000003', 'Kakadeo'),
('20000000-0000-0000-0000-000000000003', 'Geeta Nagar'),
('20000000-0000-0000-0000-000000000004', 'Kanpur'),
('20000000-0000-0000-0000-000000000004', 'Govind Nagar'),
('20000000-0000-0000-0000-000000000005', 'Kanpur'),
('20000000-0000-0000-0000-000000000005', 'Swaroop Nagar'),
('20000000-0000-0000-0000-000000000005', 'Civil Lines'),
('20000000-0000-0000-0000-000000000006', 'Delhi NCR'),
('20000000-0000-0000-0000-000000000006', 'Saket'),
('20000000-0000-0000-0000-000000000006', 'Vasant Kunj'),
('20000000-0000-0000-0000-000000000007', 'Delhi NCR'),
('20000000-0000-0000-0000-000000000007', 'Noida'),
('20000000-0000-0000-0000-000000000007', 'Noida Sector 62'),
('20000000-0000-0000-0000-000000000008', 'Delhi NCR'),
('20000000-0000-0000-0000-000000000008', 'Gurgaon'),
('20000000-0000-0000-0000-000000000008', 'DLF Phase 3'),
('20000000-0000-0000-0000-000000000009', 'Delhi NCR'),
('20000000-0000-0000-0000-000000000009', 'Connaught Place'),
('20000000-0000-0000-0000-000000000010', 'Delhi NCR'),
('20000000-0000-0000-0000-000000000010', 'Karol Bagh'),
('20000000-0000-0000-0000-000000000010', 'Rajendra Nagar');

-- 6. INSERT PORTFOLIO ITEMS WITH PREMIUM BEFORE/AFTER IMAGES
INSERT INTO portfolio_items (id, worker_id, title, description, before_image_url, after_image_url, video_url, category) VALUES
-- Ramesh (Carpenter)
('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Modular Kitchen Install', 'Designed and constructed a full custom modular kitchen with soft-close drawers and premium laminate finish.', 'https://images.unsplash.com/photo-1581858726788-75bc0f6a952d?w=500', 'https://images.unsplash.com/photo-1556912173-3bb406ef7e77?w=500', NULL, 'CARPENTER'),
('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000001', 'Wardrobe Customization', 'Fitted a 6-door floor-to-ceiling bedroom wardrobe with sliding glass doors.', 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=500', 'https://images.unsplash.com/photo-1616046229478-9901c5536a45?w=500', NULL, 'CARPENTER'),

-- Suresh (Plumber)
('30000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000002', 'Bathroom Piping Upgrade', 'Replaced leaking iron pipes with corrosion-proof CPVC plumbing inside a customer master bathroom.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', NULL, 'PLUMBER'),

-- Amit (Electrician)
('30000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000003', 'Breaker Box Overhaul', 'Upgraded a dangerous, ungrounded fuse box to an organized 12-way MCB distribution board with surge protection.', 'https://images.unsplash.com/photo-1558244661-d248897f7bc4?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'ELECTRICIAN'),

-- Vikash (Painter)
('30000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000004', 'Living Room Wall Makeover', 'Repaired cracks with putty, added waterproof base, and finished with deep royal-blue acrylic emulsion and texture.', 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500', 'https://images.unsplash.com/photo-1562259949-e8e7689d7828?w=500', NULL, 'PAINTER'),

-- Rajesh (AC Tech)
('30000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000005', 'Split AC Servicing & Fixing', 'Fixed a non-cooling split AC by repairing a gas leak, refilling R32 gas, and chemically deep cleaning the coil.', 'https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500', 'https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=500', NULL, 'AC_TECHNICIAN'),

-- Anil (Carpenter)
('30000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000006', 'TV Console Wood Unit', 'Custom built floating TV console board with hidden cabling ports and LED strip channels.', 'https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=500', 'https://images.unsplash.com/photo-1538688525198-9b88f6f53126?w=500', NULL, 'CARPENTER'),

-- Sunil (Plumber)
('30000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000007', 'Shower Fixture Replacement', 'Installed premium matte-black overhead rainfall showers and mixer taps.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', NULL, 'PLUMBER'),

-- Deepak (Electrician)
('30000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000008', 'Smart Office Lighting', 'Wired automated motion sensors and smart dimmer switches across a 3-room layout.', 'https://images.unsplash.com/photo-1558244661-d248897f7bc4?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'ELECTRICIAN'),

-- Karan (Painter)
('30000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000009', 'Accent Wall Painting', 'Painted a modern geometric pattern with gold stencil overlays on a bedroom feature wall.', 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500', 'https://images.unsplash.com/photo-1562259949-e8e7689d7828?w=500', NULL, 'PAINTER'),

-- Manish (AC Tech)
('30000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000010', 'Industrial HVAC Installation', 'Installed a 5-ton ductable AC system for a large office floor space.', 'https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500', 'https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=500', NULL, 'AC_TECHNICIAN');
