-- ====================================================================
-- KaamWala Mock Data Seed Script
-- Idempotent insertions for 22 workers covering all 11 categories in Kanpur & Delhi NCR
-- ====================================================================

-- 1. CLEANUP MOCK RECORDS TO ENABLE RE-RUNS
DELETE FROM worker_skills WHERE worker_profile_id IN (
    SELECT id FROM worker_profiles WHERE user_id BETWEEN '10000000-0000-0000-0000-000000000001' AND '10000000-0000-0000-0000-000000000022'
);

DELETE FROM worker_service_areas WHERE worker_profile_id IN (
    SELECT id FROM worker_profiles WHERE user_id BETWEEN '10000000-0000-0000-0000-000000000001' AND '10000000-0000-0000-0000-000000000022'
);

DELETE FROM portfolio_items WHERE worker_id BETWEEN '10000000-0000-0000-0000-000000000001' AND '10000000-0000-0000-0000-000000000022';

DELETE FROM worker_profiles WHERE user_id BETWEEN '10000000-0000-0000-0000-000000000001' AND '10000000-0000-0000-0000-000000000022';

DELETE FROM users WHERE id BETWEEN '10000000-0000-0000-0000-000000000001' AND '10000000-0000-0000-0000-000000000022';

-- 2. INSERT USERS (Role: WORKER)
-- Swaroop Nagar, Kalyanpur, Govind Nagar, Kakadeo in Kanpur (lat ~26.45, lng ~80.33)
-- Saket, Noida, Gurgaon, Connaught Place, Karol Bagh in Delhi NCR (lat ~28.70, lng ~77.10)
INSERT INTO users (id, name, phone, email, avatar_url, role, latitude, longitude, is_active) VALUES
-- Kanpur Workers (1-11)
('10000000-0000-0000-0000-000000000001', 'Ramesh Kumar', '+919876543201', 'ramesh.kumar@example.com', 'https://images.unsplash.com/photo-1540569014015-19a7be504e3a?w=150', 'WORKER', 26.4744, 80.3168, true),
('10000000-0000-0000-0000-000000000002', 'Amit Gupta', '+919876543202', 'amit.gupta@example.com', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 'WORKER', 26.4764, 80.2974, true),
('10000000-0000-0000-0000-000000000003', 'Suresh Singh', '+919876543203', 'suresh.singh@example.com', 'https://images.unsplash.com/photo-1566492031773-4f4e44671857?w=150', 'WORKER', 26.5126, 80.2452, true),
('10000000-0000-0000-0000-000000000004', 'Vikash Yadav', '+919876543204', 'vikash.yadav@example.com', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', 'WORKER', 26.4402, 80.3204, true),
('10000000-0000-0000-0000-000000000005', 'Rajesh Verma', '+919876543205', 'rajesh.verma@example.com', 'https://images.unsplash.com/photo-1519085360753-af0119f7cbe7?w=150', 'WORKER', 26.4800, 80.3350, true),
('10000000-0000-0000-0000-000000000006', 'Kamla Devi', '+919876543206', 'kamla.devi@example.com', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150', 'WORKER', 26.4950, 80.2600, true),
('10000000-0000-0000-0000-000000000007', 'Mohan Lal', '+919876543207', 'mohan.lal@example.com', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150', 'WORKER', 26.4680, 80.3010, true),
('10000000-0000-0000-0000-000000000008', 'Ram Singh', '+919876543208', 'ram.singh@example.com', 'https://images.unsplash.com/photo-1513956589380-bad6acb9b9d4?w=150', 'WORKER', 26.4350, 80.3420, true),
('10000000-0000-0000-0000-000000000009', 'Jagdish Prasad', '+919876543209', 'jagdish.prasad@example.com', 'https://images.unsplash.com/photo-1500048993953-d23a436266cf?w=150', 'WORKER', 26.4710, 80.3200, true),
('10000000-0000-0000-0000-000000000010', 'Nitin Sharma', '+919876543210', 'nitin.sharma@example.com', 'https://images.unsplash.com/photo-1501196354995-cbb51c65aaea?w=150', 'WORKER', 26.4820, 80.2920, true),
('10000000-0000-0000-0000-000000000011', 'Sonu Kashyap', '+919876543211', 'sonu.kashyap@example.com', 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150', 'WORKER', 26.5010, 80.2520, true),
-- Delhi NCR Workers (12-22)
('10000000-0000-0000-0000-000000000012', 'Anil Sharma', '+919876543212', 'anil.sharma@example.com', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', 'WORKER', 28.5244, 77.2066, true),
('10000000-0000-0000-0000-000000000013', 'Deepak Mishra', '+919876543213', 'deepak.mishra@example.com', 'https://images.unsplash.com/photo-1492562080023-ab3db95bfbce?w=150', 'WORKER', 28.4900, 77.0800, true),
('10000000-0000-0000-0000-000000000014', 'Sunil Patel', '+919876543214', 'sunil.patel@example.com', 'https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?w=150', 'WORKER', 28.6273, 77.3725, true),
('10000000-0000-0000-0000-000000000015', 'Karan Johar', '+919876543215', 'karan.johar@example.com', 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=150', 'WORKER', 28.6304, 77.2177, true),
('10000000-0000-0000-0000-000000000016', 'Manish Malhotra', '+919876543216', 'manish.malhotra@example.com', 'https://images.unsplash.com/photo-1489980508314-941910ded1f4?w=150', 'WORKER', 28.6500, 77.1900, true),
('10000000-0000-0000-0000-000000000017', 'Preeti Singh', '+919876543217', 'preeti.singh@example.com', 'https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=150', 'WORKER', 28.6100, 77.3600, true),
('10000000-0000-0000-0000-000000000018', 'Sanjay Dutt', '+919876543218', 'sanjay.dutt@example.com', 'https://images.unsplash.com/photo-1520341280432-4749d4d7bcf9?w=150', 'WORKER', 28.5300, 77.2200, true),
('10000000-0000-0000-0000-000000000019', 'Harpal Singh', '+919876543219', 'harpal.singh@example.com', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150', 'WORKER', 28.4800, 77.0900, true),
('10000000-0000-0000-0000-000000000020', 'Vijay Yadav', '+919876543220', 'vijay.yadav@example.com', 'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=150', 'WORKER', 28.6250, 77.2150, true),
('10000000-0000-0000-0000-000000000021', 'Rahul Verma', '+919876543221', 'rahul.verma@example.com', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 'WORKER', 28.6000, 77.3800, true),
('10000000-0000-0000-0000-000000000022', 'Manoj Kumar', '+919876543222', 'manoj.kumar@example.com', 'https://images.unsplash.com/photo-1542909168-82c3e7fdca5c?w=150', 'WORKER', 28.6450, 77.1850, true);

-- 3. INSERT WORKER PROFILES
INSERT INTO worker_profiles (id, user_id, starting_price, is_verified, aadhaar_verified, pan_verified, selfie_verified, rating_avg, total_jobs, total_earnings, bio, subscription_tier, availability_status) VALUES
-- Kanpur (1-11)
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 300.00, true, true, true, true, 4.85, 42, 12600.00, 'Experienced carpenter specializing in modular kitchens, wardrobes, and wooden door repairs.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 180.00, true, true, true, true, 4.70, 75, 13500.00, 'Licensed electrician for house wiring, inverter setups, smart switches, and maintenance.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 250.00, true, true, false, true, 4.50, 18, 4500.00, 'Professional plumber handling kitchen leak repairs, toilet fittings, blockages, and water motor setup.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 500.00, false, false, false, false, 4.20, 10, 5000.00, 'Vibrant interior and exterior house painter. Wall putty, texture designs, and wood polishing.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000005', 400.00, true, true, true, true, 4.90, 110, 44000.00, 'AC mechanic expert in gas refilling, split & window AC installation, wet washing, and repairs.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000006', 350.00, true, true, false, true, 4.80, 25, 8750.00, 'Deep home cleaning, regular floor scrubbing, bathroom sanitization, and kitchen degreasing specialist.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000007', 450.00, true, true, true, true, 4.65, 38, 17100.00, 'Skillful worker for furniture repair, wooden sofa alignment, chair upholstery fixes, and varnish touchups.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000008', 600.00, true, true, false, false, 4.40, 15, 9000.00, 'Expert mason for brickwork, tiling, marble polishing, plastering, and cement repair work.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000009', 400.00, true, true, true, true, 4.75, 60, 24000.00, 'Professional welder specializing in iron gates, steel grilles, safety railings, and custom fabrication.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000010', 500.00, true, true, true, true, 4.90, 80, 40000.00, 'CCTV camera installation, IP/analog NVR setup, remote mobile view configurations, and troubleshooting.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000011', 300.00, true, true, false, true, 4.60, 45, 13500.00, 'RO service expert. Filter change, TDS level tuning, membrane replacement, and complete system overhaul.', 'FREE', 'AVAILABLE'),
-- Delhi NCR (12-22)
('20000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000012', 450.00, true, true, true, true, 4.75, 54, 24300.00, 'High-end modular furniture creator and structural woodwork designer serving Delhi & NCR.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000013', 300.00, true, true, true, true, 4.80, 89, 26700.00, 'Expert electrician for commercial and residential installations, circuit board wiring, and lighting.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000014', 350.00, true, true, false, true, 4.60, 32, 11200.00, 'Certified plumber for modern bathroom renovations, leak detection, and pipeline installations.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000015', 700.00, false, false, false, false, 4.40, 15, 10500.00, 'Professional painter for luxury apartments. Wall art, metallic textures, and premium waterproof coating.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000016', 600.00, true, true, true, true, 4.95, 140, 84000.00, 'Highly-rated heating and cooling expert. 12+ years installing HVAC systems and central air conditioning.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000017', 500.00, true, true, true, true, 4.70, 90, 45000.00, 'Professional cleaning agency style deep sanitization, vacuuming, and disinfection of homes.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000018', 800.00, true, true, false, true, 4.55, 20, 16000.00, 'Premium furniture restoration and customized sofa set, dining table repairing specialists.', 'FREE', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000019', '10000000-0000-0000-0000-000000000019', 900.00, true, true, true, true, 4.88, 65, 58500.00, 'Senior mason for premium marble tiling, wall tiling, outdoor landscaping, paving, and wall construction.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000020', '10000000-0000-0000-0000-000000000020', 550.00, true, true, true, true, 4.60, 48, 26400.00, 'Heavy industrial welding, metal structure installation, laser cutting, and commercial shutter repairs.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000021', '10000000-0000-0000-0000-000000000021', 800.00, true, true, false, true, 4.78, 102, 81600.00, 'High-definition IP cameras, access controls, biometric sensors integration, and home automation security.', 'PREMIUM', 'AVAILABLE'),
('20000000-0000-0000-0000-000000000022', '10000000-0000-0000-0000-000000000022', 400.00, true, true, true, true, 4.82, 115, 46000.00, 'Commercial RO filter plant servicing, domestic alkaline filter installations, and mineral cartridge updates.', 'PREMIUM', 'AVAILABLE');

-- 4. INSERT WORKER SKILLS (Using exact 11 Categories)
INSERT INTO worker_skills (worker_profile_id, skill) VALUES
-- Kanpur
('20000000-0000-0000-0000-000000000001', 'CARPENTER'),
('20000000-0000-0000-0000-000000000002', 'ELECTRICIAN'),
('20000000-0000-0000-0000-000000000003', 'PLUMBER'),
('20000000-0000-0000-0000-000000000004', 'PAINTER'),
('20000000-0000-0000-0000-000000000005', 'AC_TECHNICIAN'),
('20000000-0000-0000-0000-000000000006', 'HOME_CLEANING'),
('20000000-0000-0000-0000-000000000007', 'FURNITURE_MAKER'),
('20000000-0000-0000-0000-000000000008', 'MASON'),
('20000000-0000-0000-0000-000000000009', 'WELDER'),
('20000000-0000-0000-0000-000000000010', 'CCTV_INSTALLER'),
('20000000-0000-0000-0000-000000000011', 'RO_SERVICE'),
-- Delhi NCR
('20000000-0000-0000-0000-000000000012', 'CARPENTER'),
('20000000-0000-0000-0000-000000000013', 'ELECTRICIAN'),
('20000000-0000-0000-0000-000000000014', 'PLUMBER'),
('20000000-0000-0000-0000-000000000015', 'PAINTER'),
('20000000-0000-0000-0000-000000000016', 'AC_TECHNICIAN'),
('20000000-0000-0000-0000-000000000017', 'HOME_CLEANING'),
('20000000-0000-0000-0000-000000000018', 'FURNITURE_MAKER'),
('20000000-0000-0000-0000-000000000019', 'MASON'),
('20000000-0000-0000-0000-000000000020', 'WELDER'),
('20000000-0000-0000-0000-000000000021', 'CCTV_INSTALLER'),
('20000000-0000-0000-0000-000000000022', 'RO_SERVICE');

-- 5. INSERT WORKER SERVICE AREAS
INSERT INTO worker_service_areas (worker_profile_id, area) VALUES
-- Kanpur
('20000000-0000-0000-0000-000000000001', 'Kanpur'), ('20000000-0000-0000-0000-000000000001', 'Swaroop Nagar'),
('20000000-0000-0000-0000-000000000002', 'Kanpur'), ('20000000-0000-0000-0000-000000000002', 'Kakadeo'),
('20000000-0000-0000-0000-000000000003', 'Kanpur'), ('20000000-0000-0000-0000-000000000003', 'Kalyanpur'),
('20000000-0000-0000-0000-000000000004', 'Kanpur'), ('20000000-0000-0000-0000-000000000004', 'Govind Nagar'),
('20000000-0000-0000-0000-000000000005', 'Kanpur'), ('20000000-0000-0000-0000-000000000005', 'Swaroop Nagar'),
('20000000-0000-0000-0000-000000000006', 'Kanpur'), ('20000000-0000-0000-0000-000000000006', 'Kalyanpur'),
('20000000-0000-0000-0000-000000000007', 'Kanpur'), ('20000000-0000-0000-0000-000000000007', 'Kakadeo'),
('20000000-0000-0000-0000-000000000008', 'Kanpur'), ('20000000-0000-0000-0000-000000000008', 'Govind Nagar'),
('20000000-0000-0000-0000-000000000009', 'Kanpur'), ('20000000-0000-0000-0000-000000000009', 'Swaroop Nagar'),
('20000000-0000-0000-0000-000000000010', 'Kanpur'), ('20000000-0000-0000-0000-000000000010', 'Kakadeo'),
('20000000-0000-0000-0000-000000000011', 'Kanpur'), ('20000000-0000-0000-0000-000000000011', 'Kalyanpur'),
-- Delhi NCR
('20000000-0000-0000-0000-000000000012', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000012', 'Saket'),
('20000000-0000-0000-0000-000000000013', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000013', 'Gurgaon'),
('20000000-0000-0000-0000-000000000014', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000014', 'Noida'),
('20000000-0000-0000-0000-000000000015', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000015', 'Connaught Place'),
('20000000-0000-0000-0000-000000000016', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000016', 'Karol Bagh'),
('20000000-0000-0000-0000-000000000017', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000017', 'Noida'),
('20000000-0000-0000-0000-000000000018', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000018', 'Saket'),
('20000000-0000-0000-0000-000000000019', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000019', 'Gurgaon'),
('20000000-0000-0000-0000-000000000020', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000020', 'Connaught Place'),
('20000000-0000-0000-0000-000000000021', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000021', 'Noida'),
('20000000-0000-0000-0000-000000000022', 'Delhi NCR'), ('20000000-0000-0000-0000-000000000022', 'Karol Bagh');

-- 6. INSERT PORTFOLIO ITEMS WITH PREMIUM BEFORE/AFTER IMAGES
INSERT INTO portfolio_items (id, worker_id, title, description, before_image_url, after_image_url, video_url, category) VALUES
-- Ramesh (Carpenter)
('30000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'Modular Kitchen Setup', 'Fitted modular kitchen cabinets with auto-close drawers.', 'https://images.unsplash.com/photo-1581858726788-75bc0f6a952d?w=500', 'https://images.unsplash.com/photo-1556912173-3bb406ef7e77?w=500', NULL, 'CARPENTER'),
-- Amit (Electrician)
('30000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'Main Switch Board Upgrade', 'Upgraded a manual distribution box with modern automatic MCBs.', 'https://images.unsplash.com/photo-1558244661-d248897f7bc4?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'ELECTRICIAN'),
-- Suresh (Plumber)
('30000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'Master Bathroom Piping', 'Replaced ancient corroded metal pipeline with leak-proof CPVC lines.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', NULL, 'PLUMBER'),
-- Vikash (Painter)
('30000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 'Accent Wall Texture', 'Applied premium royal texture coating on living room wall.', 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500', 'https://images.unsplash.com/photo-1562259949-e8e7689d7828?w=500', NULL, 'PAINTER'),
-- Rajesh (AC Tech)
('30000000-0000-0000-0000-000000000005', '10000000-0000-0000-0000-000000000005', 'AC Servicing & Gas Refill', 'Diagnosed gas leak and recharged with environment friendly gas.', 'https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500', 'https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=500', NULL, 'AC_TECHNICIAN'),
-- Kamla (Home Cleaning)
('30000000-0000-0000-0000-000000000006', '10000000-0000-0000-0000-000000000006', 'Full House Deep Cleaning', 'Deep scrubbed and vacuumed entire house floor and cabinets.', 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=500', 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=500', NULL, 'HOME_CLEANING'),
-- Mohan (Furniture Repair)
('30000000-0000-0000-0000-000000000007', '10000000-0000-0000-0000-000000000007', 'Sofa Set Re-Upholstery', 'Repaired broken frame and refitted premium leather fabric.', 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=500', 'https://images.unsplash.com/photo-1616046229478-9901c5536a45?w=500', NULL, 'FURNITURE_MAKER'),
-- Ram (Mason)
('30000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000008', 'Bathroom Floor Tiling', 'Leveled flooring and installed customized anti-skid floor tiles.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', NULL, 'MASON'),
-- Jagdish (Welder)
('30000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000009', 'Balcony Railing Assembly', 'Designed and welded customized safety iron grills for balcony.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'WELDER'),
-- Nitin (CCTV Installer)
('30000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000010', 'IP Dome Camera Wiring', 'Wired 4 HD CCTV cameras with NVR storage setup.', 'https://images.unsplash.com/photo-1558244661-d248897f7bc4?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'CCTV_INSTALLER'),
-- Sonu (RO Service)
('30000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000011', 'RO Filter Replacement', 'Swapped worn out filters and adjusted TDS levels for pure water.', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', 'https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=500', NULL, 'RO_SERVICE'),

-- Delhi NCR Portfolios (Anil 12 - Manoj 22)
('30000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000012', 'Designer TV Console Unit', 'Bespoke floating wall-mounted wooden console assembly.', 'https://images.unsplash.com/photo-1600585154526-990dced4db0d?w=500', 'https://images.unsplash.com/photo-1538688525198-9b88f6f53126?w=500', NULL, 'CARPENTER'),
('30000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000013', 'Smart Office Ceiling Lights', 'Installed recessed smart dimming LED lights inside a Gurgaon workplace.', 'https://images.unsplash.com/photo-1558244661-d248897f7bc4?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'ELECTRICIAN'),
('30000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000014', 'Shower System Setup', 'Fitted modern multi-flow rainfall shower panels in master bath.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', NULL, 'PLUMBER'),
('30000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000015', 'Full Apartment Wall Polishing', 'Putty patching and high-gloss plastic emulsion painting.', 'https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500', 'https://images.unsplash.com/photo-1562259949-e8e7689d7828?w=500', NULL, 'PAINTER'),
('30000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000016', 'Ductable HVAC Setup', 'Mounted central AC units across a dual floor layout.', 'https://images.unsplash.com/photo-1621905251189-08b45d6a269e?w=500', 'https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=500', NULL, 'AC_TECHNICIAN'),
('30000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000017', 'Villa Sanitization & Vacuuming', 'Professional grade deep vacuuming of carpets and sofas.', 'https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=500', 'https://images.unsplash.com/photo-1527515637462-cff94eecc1ac?w=500', NULL, 'HOME_CLEANING'),
('30000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000018', 'Classic Dining Table Repair', 'Repaired broken leg support and polished wood grains.', 'https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=500', 'https://images.unsplash.com/photo-1616046229478-9901c5536a45?w=500', NULL, 'FURNITURE_MAKER'),
('30000000-0000-0000-0000-000000000019', '10000000-0000-0000-0000-000000000019', 'Marble Floor Restoration', 'Re-polished floor stones and leveled mortar borders.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', NULL, 'MASON'),
('30000000-0000-0000-0000-000000000020', '10000000-0000-0000-0000-000000000020', 'Stainless Steel Gate Welds', 'Fabricated sliding gateway frame and safety grill bars.', 'https://images.unsplash.com/photo-1504328345606-18bbc8c9d7d1?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'WELDER'),
('30000000-0000-0000-0000-000000000021', '10000000-0000-0000-0000-000000000021', 'NVR Server Security Setup', 'Wired biometric locks and integrated CCTV stream with NVR console.', 'https://images.unsplash.com/photo-1558244661-d248897f7bc4?w=500', 'https://images.unsplash.com/photo-1621905252507-b354bc25edac?w=500', NULL, 'CCTV_INSTALLER'),
('30000000-0000-0000-0000-000000000022', '10000000-0000-0000-0000-000000000022', 'Alkaline Mineral Filter Install', 'Integrated inline mineralizers and sediment pre-filter taps.', 'https://images.unsplash.com/photo-1584622650111-993a426fbf0a?w=500', 'https://images.unsplash.com/photo-1527689368864-3a821dbccc34?w=500', NULL, 'RO_SERVICE');
