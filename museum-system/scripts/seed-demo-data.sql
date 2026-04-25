SET @admin_id := (SELECT id FROM user WHERE username = 'admin' LIMIT 1);
SET @admin_id := IFNULL(@admin_id, 1);

DELETE FROM role_permission WHERE role_id IS NULL;

DELETE FROM disposal_flow
WHERE disposal_id IN (SELECT id FROM disposal WHERE artifact_code LIKE 'DEMO-%');

DELETE FROM restoration_flow
WHERE restoration_id IN (SELECT id FROM restoration WHERE artifact_code LIKE 'DEMO-%');

DELETE FROM outbound_flow
WHERE outbound_id IN (SELECT id FROM outbound WHERE artifact_code LIKE 'DEMO-%');

DELETE FROM loan_flow
WHERE loan_id IN (SELECT id FROM loan WHERE artifact_code LIKE 'DEMO-%');

DELETE FROM visitor_appointment WHERE visitor_phone LIKE '1390000%';
DELETE FROM exhibition_artifact WHERE exhibition_id IN (SELECT id FROM exhibition WHERE name LIKE 'Demo %');
DELETE FROM exhibition WHERE name LIKE 'Demo %';
DELETE FROM warehouse_in WHERE artifact_id IN (SELECT id FROM artifact WHERE code LIKE 'DEMO-%');
DELETE FROM warehousing WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM appraisal WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM acquisition WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM storage_check WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM disposal WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM restoration WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM outbound WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM loan WHERE artifact_code LIKE 'DEMO-%';
DELETE FROM artifact WHERE code LIKE 'DEMO-%';

INSERT INTO artifact (
  code,
  name,
  category,
  era,
  material,
  size,
  weight,
  source,
  preservation_status,
  appraisal_level,
  current_state,
  image_url,
  documents,
  environment_requirements,
  insurance_info,
  location
) VALUES
(
  'DEMO-ART-001',
  'Demo Bronze Vessel',
  'Bronze',
  'Han',
  'Bronze',
  '32cm',
  2.40,
  'Social Collection',
  'GOOD',
  'LEVEL_1',
  'IN_STORAGE',
  'https://images.unsplash.com/photo-1577083552431-6e5fd01aa342',
  'collection-agreement.pdf,appraisal-report.pdf',
  '{"temperature":"18-24C","humidity":"45%-55%","light":"Low","notes":"Keep away from direct light"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-001","coverage":"800000"}',
  'A-01-01'
),
(
  'DEMO-ART-002',
  'Demo Gilded Statue',
  'Metal',
  'Tang',
  'Copper Alloy',
  '45cm',
  8.20,
  'Inter-museum Transfer',
  'MINOR_DAMAGE',
  'LEVEL_1',
  'IN_STORAGE',
  'https://images.unsplash.com/photo-1544967082-d9d25d867d66',
  'transfer-order.pdf,repair-note.pdf',
  '{"temperature":"20-24C","humidity":"45%-55%","light":"Low","notes":"Stable environment required"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-002","coverage":"1500000"}',
  'A-02-03'
),
(
  'DEMO-ART-003',
  'Demo Landscape Scroll',
  'Painting',
  'Ming',
  'Paper',
  '180cm x 68cm',
  0.80,
  'Donation',
  'GOOD',
  'LEVEL_2',
  'ON_EXHIBITION',
  'https://images.unsplash.com/photo-1579783901586-d88db74b4fe4',
  'donation-certificate.pdf',
  '{"temperature":"18-22C","humidity":"50%","light":"Very low","notes":"Use low-light display only"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-003","coverage":"600000"}',
  'Gallery-02'
),
(
  'DEMO-ART-004',
  'Demo Ritual Vessel',
  'Bronze',
  'Shang',
  'Bronze',
  '58cm',
  35.50,
  'Archaeological Excavation',
  'STABLE',
  'LEVEL_1',
  'IN_STORAGE',
  'https://images.unsplash.com/photo-1564399580075-5dfe19c205f3',
  'excavation-record.pdf',
  '{"temperature":"18-22C","humidity":"40%-50%","light":"Low","notes":"Monitor oxidation"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-004","coverage":"3000000"}',
  'B-01'
),
(
  'DEMO-ART-005',
  'Demo Painted Pottery',
  'Pottery',
  'Han',
  'Clay',
  '52cm',
  6.30,
  'Archaeological Excavation',
  'DAMAGED',
  'LEVEL_2',
  'RESTORATION',
  'https://images.unsplash.com/photo-1592728323279-83a7d8f1a6d4',
  'condition-record.pdf',
  '{"temperature":"18-24C","humidity":"45%-55%","light":"Low","notes":"Needs restoration follow-up"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-005","coverage":"350000"}',
  'Restoration-Room-02'
),
(
  'DEMO-ART-006',
  'Demo Jade Ornament',
  'Jade',
  'Qing',
  'Hetian Jade',
  '9cm',
  0.12,
  'Purchase',
  'GOOD',
  'LEVEL_3',
  'IN_STORAGE',
  'https://images.unsplash.com/photo-1617038260897-41a1f14a8ca0',
  'purchase-invoice.pdf',
  '{"temperature":"18-24C","humidity":"45%-55%","light":"Low","notes":"Keep in separate box"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-006","coverage":"120000"}',
  'Treasure-03'
),
(
  'DEMO-ART-007',
  'Demo Painted Plate',
  'Porcelain',
  'Qing',
  'Porcelain',
  '28cm',
  1.10,
  'Donation',
  'GOOD',
  'LEVEL_3',
  'IN_STORAGE',
  'https://images.unsplash.com/photo-1610701596007-11502861dcfa',
  'donation-letter.pdf',
  '{"temperature":"18-24C","humidity":"45%-55%","light":"Low","notes":"Shock-proof handling"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-007","coverage":"90000"}',
  'A-03-02'
),
(
  'DEMO-ART-008',
  'Demo Lacquer Screen',
  'Woodwork',
  'Republic Era',
  'Wood and Lacquer',
  '220cm x 180cm',
  42.00,
  'Social Collection',
  'DAMAGED',
  'LEVEL_2',
  'OUTBOUND',
  'https://images.unsplash.com/photo-1586023492125-27b2c045efd7',
  'transport-plan.pdf',
  '{"temperature":"18-24C","humidity":"45%-55%","light":"Low","notes":"Avoid insects and moisture swings"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-008","coverage":"450000"}',
  'Transit-Area'
),
(
  'DEMO-ART-009',
  'Demo Rubbing Album',
  'Document',
  'Song',
  'Paper',
  '30 pages',
  0.60,
  'Collection Arrangement',
  'AGING',
  'LEVEL_3',
  'IN_STORAGE',
  'https://images.unsplash.com/photo-1519682337058-a94d519337bc',
  'arrangement-list.pdf',
  '{"temperature":"16-20C","humidity":"40%-50%","light":"Very low","notes":"Store in archival box"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-009","coverage":"80000"}',
  'Document-C02'
),
(
  'DEMO-ART-010',
  'Demo Silver Incense Box',
  'Metalwork',
  'Tang',
  'Silver',
  '6cm',
  0.18,
  'Purchase',
  'GOOD',
  'LEVEL_2',
  'ON_LOAN',
  'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338',
  'loan-agreement.pdf',
  '{"temperature":"18-24C","humidity":"45%-55%","light":"Low","notes":"Keep sealed to avoid oxidation"}',
  '{"company":"Museum Insurance","policyNo":"MI-2026-010","coverage":"500000"}',
  'On-Loan City Museum'
);

INSERT INTO acquisition (
  artifact_id,
  artifact_code,
  artifact_name,
  acquisition_date,
  acquisition_type,
  acquisition_cost,
  donor_info,
  source_institution,
  source_contact,
  source_phone,
  remarks,
  created_by
)
SELECT
  id,
  code,
  name,
  DATE_SUB(CURDATE(), INTERVAL id DAY),
  source,
  50000 + id * 1000,
  'Demo donor',
  'Demo source institution',
  'Curator Wang',
  '13800000001',
  'Demo acquisition record',
  @admin_id
FROM artifact
WHERE code LIKE 'DEMO-ART-%';

INSERT INTO appraisal (
  artifact_id,
  artifact_code,
  artifact_name,
  appraisal_date,
  expert_name,
  expert_title,
  expert_opinion,
  appraisal_level,
  appraisal_result,
  appraisal_fee,
  estimated_value,
  attachment,
  remarks,
  created_by
)
SELECT
  id,
  code,
  name,
  DATE_SUB(CURDATE(), INTERVAL 20 DAY),
  'Li Ming',
  'Research Fellow',
  'Demo appraisal opinion: suitable for long-term museum preservation.',
  appraisal_level,
  'PASS',
  3000.00,
  100000 + id * 50000,
  'demo-appraisal.pdf',
  'Demo appraisal record',
  @admin_id
FROM artifact
WHERE code IN (
  'DEMO-ART-001',
  'DEMO-ART-002',
  'DEMO-ART-003',
  'DEMO-ART-004',
  'DEMO-ART-005',
  'DEMO-ART-006',
  'DEMO-ART-007',
  'DEMO-ART-008'
);

INSERT INTO warehousing (
  artifact_id,
  artifact_code,
  artifact_name,
  storage_date,
  storage_area,
  storage_shelf,
  storage_position,
  storage_status,
  temperature,
  humidity,
  handler,
  remarks,
  created_by
)
SELECT
  id,
  code,
  name,
  DATE_SUB(CURDATE(), INTERVAL 10 DAY),
  CASE
    WHEN category IN ('Painting', 'Document') THEN 'Climate Archive'
    WHEN category IN ('Bronze', 'Metal', 'Metalwork') THEN 'Metal Storage'
    ELSE 'General Storage'
  END,
  CONCAT('S-', LPAD(id, 2, '0')),
  CONCAT('P-', LPAD(id, 2, '0')),
  'NORMAL',
  '20C',
  '50%',
  'admin',
  'Demo warehousing record',
  @admin_id
FROM artifact
WHERE code LIKE 'DEMO-ART-%';

INSERT INTO warehouse_in (artifact_id, in_date, location, operator, remarks)
SELECT
  id,
  DATE_SUB(NOW(), INTERVAL 9 DAY),
  location,
  @admin_id,
  'Demo warehouse-in record'
FROM artifact
WHERE code LIKE 'DEMO-ART-%';

INSERT INTO exhibition (
  name,
  theme,
  start_date,
  end_date,
  venue,
  curator,
  description,
  poster_url,
  status,
  created_by
) VALUES
(
  'Demo Bronze Civilization',
  'Bronze culture and ritual order',
  DATE_SUB(CURDATE(), INTERVAL 15 DAY),
  DATE_ADD(CURDATE(), INTERVAL 45 DAY),
  'Hall 1',
  'Curator Chen',
  'Demo exhibition for bronze artifacts.',
  'https://images.unsplash.com/photo-1566127444979-b3d2b654e3d7',
  'ONGOING',
  @admin_id
),
(
  'Demo Silk Road Exchange',
  'Trade and craftsmanship along the Silk Road',
  DATE_ADD(CURDATE(), INTERVAL 10 DAY),
  DATE_ADD(CURDATE(), INTERVAL 70 DAY),
  'Hall 2',
  'Curator Zhao',
  'Demo planning exhibition.',
  'https://images.unsplash.com/photo-1564399580075-5dfe19c205f3',
  'PLANNING',
  @admin_id
),
(
  'Demo Painting Showcase',
  'Landscape and documents display',
  DATE_SUB(CURDATE(), INTERVAL 5 DAY),
  DATE_ADD(CURDATE(), INTERVAL 30 DAY),
  'Hall 3',
  'Curator Zhou',
  'Demo exhibition for painting and document collections.',
  'https://images.unsplash.com/photo-1579783901586-d88db74b4fe4',
  'ONGOING',
  @admin_id
);

INSERT INTO exhibition_artifact (exhibition_id, artifact_id, display_order, remark)
SELECT e.id, a.id, 1, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Bronze Civilization' AND a.code = 'DEMO-ART-002'
UNION ALL
SELECT e.id, a.id, 2, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Bronze Civilization' AND a.code = 'DEMO-ART-004'
UNION ALL
SELECT e.id, a.id, 3, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Bronze Civilization' AND a.code = 'DEMO-ART-010'
UNION ALL
SELECT e.id, a.id, 1, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Silk Road Exchange' AND a.code = 'DEMO-ART-001'
UNION ALL
SELECT e.id, a.id, 2, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Silk Road Exchange' AND a.code = 'DEMO-ART-006'
UNION ALL
SELECT e.id, a.id, 3, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Silk Road Exchange' AND a.code = 'DEMO-ART-008'
UNION ALL
SELECT e.id, a.id, 1, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Painting Showcase' AND a.code = 'DEMO-ART-003'
UNION ALL
SELECT e.id, a.id, 2, 'Demo exhibition artifact'
FROM exhibition e
JOIN artifact a ON e.name = 'Demo Painting Showcase' AND a.code = 'DEMO-ART-009';

INSERT INTO visitor_appointment (
  exhibition_id,
  visitor_name,
  visitor_phone,
  visitor_id_card,
  appointment_date,
  appointment_time_slot,
  visitor_count,
  status,
  remark,
  created_by,
  created_time,
  updated_time
)
SELECT
  e.id,
  CONCAT('Demo Visitor ', n.n),
  CONCAT('1390000', LPAD(n.n, 4, '0')),
  CONCAT('11010119900101', LPAD(n.n, 4, '0')),
  DATE_ADD(CURDATE(), INTERVAL n.n DAY),
  CASE WHEN MOD(n.n, 2) = 0 THEN 'AM' ELSE 'PM' END,
  1 + MOD(n.n, 4),
  CASE WHEN MOD(n.n, 3) = 0 THEN 'APPROVED' ELSE 'PENDING' END,
  'Demo appointment',
  @admin_id,
  NOW(),
  NOW()
FROM exhibition e
JOIN (
  SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5
) n
WHERE e.name = 'Demo Bronze Civilization';

INSERT INTO loan (
  artifact_id,
  artifact_code,
  artifact_name,
  status,
  current_stage,
  apply_date,
  applicant,
  apply_reason,
  borrower_institution,
  borrower_contact,
  borrower_phone,
  borrower_address,
  loan_date,
  expected_return_date,
  loan_purpose,
  transport_method,
  security_measures,
  loan_location,
  loan_supervisor,
  before_condition,
  approval_opinion,
  handler,
  created_by
)
SELECT
  id,
  code,
  name,
  CASE code WHEN 'DEMO-ART-010' THEN 'LOANED' ELSE 'PENDING' END,
  CASE code WHEN 'DEMO-ART-010' THEN 'LOANED' ELSE 'APPLY' END,
  DATE_SUB(CURDATE(), INTERVAL 7 DAY),
  'admin',
  'Demo academic exchange',
  'City Museum',
  'Liu Director',
  '13700000001',
  '1 Culture Road',
  CASE code WHEN 'DEMO-ART-010' THEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) ELSE NULL END,
  DATE_ADD(CURDATE(), INTERVAL 60 DAY),
  'Special exhibition',
  'Dedicated vehicle',
  'Constant-temperature case and insurance',
  'City Museum Temporary Hall',
  'Supervisor Wang',
  'Stable before loan',
  'Demo loan approval',
  'admin',
  @admin_id
FROM artifact
WHERE code IN ('DEMO-ART-006', 'DEMO-ART-010');

INSERT INTO loan_flow (
  loan_id,
  stage_name,
  approver_name,
  approver_role,
  approval_opinion,
  status,
  approve_time,
  remarks
)
SELECT
  id,
  'APPLY',
  'admin',
  'ROLE_ADMIN',
  'Demo loan flow approval',
  'APPROVED',
  NOW(),
  'Demo loan flow'
FROM loan
WHERE artifact_code LIKE 'DEMO-%';

INSERT INTO outbound (
  artifact_id,
  artifact_code,
  artifact_name,
  outbound_date,
  expected_return_date,
  outbound_reason,
  target_exhibition,
  target_location,
  target_contact,
  target_phone,
  status,
  current_stage,
  handler,
  created_by
)
SELECT
  id,
  code,
  name,
  CASE code WHEN 'DEMO-ART-008' THEN DATE_SUB(CURDATE(), INTERVAL 2 DAY) ELSE NULL END,
  DATE_ADD(CURDATE(), INTERVAL 40 DAY),
  'Temporary exhibition',
  'Demo Silk Road Exchange',
  'Hall 2',
  'Curator Zhao',
  '13600000001',
  CASE code WHEN 'DEMO-ART-008' THEN 'OUTBOUND' ELSE 'PENDING' END,
  CASE code WHEN 'DEMO-ART-008' THEN 'APPROVED' ELSE 'STAGE1' END,
  'admin',
  @admin_id
FROM artifact
WHERE code IN ('DEMO-ART-003', 'DEMO-ART-008');

INSERT INTO outbound_flow (
  outbound_id,
  stage_name,
  approver_name,
  approver_role,
  approval_opinion,
  status,
  approve_time,
  remarks
)
SELECT
  id,
  current_stage,
  'admin',
  'ROLE_ADMIN',
  'Demo outbound approval',
  'APPROVED',
  NOW(),
  'Demo outbound flow'
FROM outbound
WHERE artifact_code LIKE 'DEMO-%';

INSERT INTO restoration (
  artifact_id,
  artifact_code,
  artifact_name,
  status,
  current_stage,
  apply_date,
  applicant,
  apply_reason,
  damage_condition,
  restoration_type,
  restoration_priority,
  plan_content,
  plan_materials,
  plan_steps,
  estimated_start_date,
  estimated_end_date,
  before_condition,
  restorer,
  supervisor,
  remarks,
  handler,
  created_by
)
SELECT
  id,
  code,
  name,
  'PENDING',
  'APPLY',
  DATE_SUB(CURDATE(), INTERVAL 4 DAY),
  'admin',
  'Demo preventive restoration request',
  'Surface condition requires treatment',
  'PROTECTION',
  'HIGH',
  'Cleaning, consolidation and documentation',
  'Neutral cleaning materials',
  'Inspection -> cleaning -> consolidation -> review',
  DATE_ADD(CURDATE(), INTERVAL 3 DAY),
  DATE_ADD(CURDATE(), INTERVAL 20 DAY),
  'Condition recorded before restoration',
  'Restorer Sun',
  'Supervisor Zhou',
  'Demo restoration record',
  'admin',
  @admin_id
FROM artifact
WHERE code IN ('DEMO-ART-005', 'DEMO-ART-009');

INSERT INTO restoration_flow (
  restoration_id,
  stage_name,
  approver_name,
  approver_role,
  approval_opinion,
  status,
  approve_time,
  remarks
)
SELECT
  id,
  'APPLY',
  'admin',
  'ROLE_ADMIN',
  'Demo restoration flow approval',
  'APPROVED',
  NOW(),
  'Demo restoration flow'
FROM restoration
WHERE artifact_code LIKE 'DEMO-%';

INSERT INTO disposal (
  artifact_id,
  artifact_code,
  artifact_name,
  status,
  current_stage,
  apply_date,
  applicant,
  apply_reason,
  disposal_type,
  evaluation_report,
  expert_opinion,
  public_start_time,
  public_end_time,
  public_result,
  remarks,
  created_by
)
SELECT
  id,
  code,
  name,
  'PENDING',
  'APPLY',
  DATE_SUB(CURDATE(), INTERVAL 2 DAY),
  'admin',
  'Demo disposal workflow test',
  'TRANSFER',
  'Suitable for educational transfer after review',
  'Experts agree to continue the disposal workflow',
  CURDATE(),
  DATE_ADD(CURDATE(), INTERVAL 7 DAY),
  'IN_PUBLICITY',
  'Demo disposal record',
  @admin_id
FROM artifact
WHERE code = 'DEMO-ART-007';

INSERT INTO disposal_flow (
  disposal_id,
  stage_name,
  approver_name,
  approver_role,
  approval_opinion,
  status,
  approve_time,
  remarks
)
SELECT
  id,
  'APPLY',
  'admin',
  'ROLE_ADMIN',
  'Demo disposal flow approval',
  'APPROVED',
  NOW(),
  'Demo disposal flow'
FROM disposal
WHERE artifact_code LIKE 'DEMO-%';

INSERT INTO storage_check (
  artifact_id,
  artifact_code,
  artifact_name,
  check_date,
  check_result,
  condition_desc,
  checker,
  check_type,
  temperature,
  humidity,
  corrosion_level,
  mildew_level,
  brittleness_level,
  light_damage_level,
  pest_damage_level,
  handling_suggestion,
  next_check_date,
  checker_name,
  remarks,
  updated_time
)
SELECT
  id,
  code,
  name,
  DATE_SUB(CURDATE(), INTERVAL MOD(id, 5) DAY),
  CASE WHEN preservation_status IN ('GOOD', 'STABLE') THEN 'NORMAL' ELSE 'ATTENTION' END,
  CONCAT(name, ' demo condition check'),
  @admin_id,
  'ROUTINE',
  '20C',
  '50%',
  'LOW',
  'LOW',
  'LOW',
  'LOW',
  'LOW',
  'Continue routine monitoring',
  DATE_ADD(CURDATE(), INTERVAL 90 DAY),
  'admin',
  'Demo storage check',
  NOW()
FROM artifact
WHERE code LIKE 'DEMO-ART-%';
