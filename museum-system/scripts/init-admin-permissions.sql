INSERT INTO role (role_code, role_name, description)
SELECT 'ROLE_ADMIN', 'System Administrator', 'Full system access'
WHERE NOT EXISTS (
  SELECT 1 FROM role WHERE role_code = 'ROLE_ADMIN'
);

INSERT INTO permission (perm_code, perm_name, parent_id, type, sort)
SELECT perm_code, perm_name, 0, 'button', sort_order
FROM (
  SELECT 'acquisition:add' perm_code, 'Acquisition Add' perm_name, 1 sort_order UNION ALL
  SELECT 'acquisition:delete', 'Acquisition Delete', 2 UNION ALL
  SELECT 'acquisition:edit', 'Acquisition Edit', 3 UNION ALL
  SELECT 'acquisition:view', 'Acquisition View', 4 UNION ALL
  SELECT 'appraisal:add', 'Appraisal Add', 5 UNION ALL
  SELECT 'appraisal:delete', 'Appraisal Delete', 6 UNION ALL
  SELECT 'appraisal:edit', 'Appraisal Edit', 7 UNION ALL
  SELECT 'appraisal:view', 'Appraisal View', 8 UNION ALL
  SELECT 'approval:delete', 'Approval Delete', 9 UNION ALL
  SELECT 'approval:process', 'Approval Process', 10 UNION ALL
  SELECT 'approval:submit', 'Approval Submit', 11 UNION ALL
  SELECT 'approval:view', 'Approval View', 12 UNION ALL
  SELECT 'artifact:add', 'Artifact Add', 13 UNION ALL
  SELECT 'artifact:delete', 'Artifact Delete', 14 UNION ALL
  SELECT 'artifact:edit', 'Artifact Edit', 15 UNION ALL
  SELECT 'artifact:view', 'Artifact View', 16 UNION ALL
  SELECT 'artifact:export', 'Artifact Export', 17 UNION ALL
  SELECT 'disposal:add', 'Disposal Add', 17 UNION ALL
  SELECT 'disposal:approve', 'Disposal Approve', 18 UNION ALL
  SELECT 'disposal:delete', 'Disposal Delete', 19 UNION ALL
  SELECT 'disposal:edit', 'Disposal Edit', 20 UNION ALL
  SELECT 'disposal:view', 'Disposal View', 21 UNION ALL
  SELECT 'exhibition:add', 'Exhibition Add', 22 UNION ALL
  SELECT 'exhibition:delete', 'Exhibition Delete', 23 UNION ALL
  SELECT 'exhibition:edit', 'Exhibition Edit', 24 UNION ALL
  SELECT 'exhibition:view', 'Exhibition View', 25 UNION ALL
  SELECT 'exhibition-evaluation:add', 'Exhibition Evaluation Add', 26 UNION ALL
  SELECT 'exhibition-evaluation:delete', 'Exhibition Evaluation Delete', 27 UNION ALL
  SELECT 'exhibition-evaluation:edit', 'Exhibition Evaluation Edit', 28 UNION ALL
  SELECT 'exhibition-evaluation:view', 'Exhibition Evaluation View', 29 UNION ALL
  SELECT 'loan:add', 'Loan Add', 30 UNION ALL
  SELECT 'loan:approve', 'Loan Approve', 31 UNION ALL
  SELECT 'loan:delete', 'Loan Delete', 32 UNION ALL
  SELECT 'loan:edit', 'Loan Edit', 33 UNION ALL
  SELECT 'loan:return', 'Loan Return', 34 UNION ALL
  SELECT 'loan:view', 'Loan View', 35 UNION ALL
  SELECT 'outbound:add', 'Outbound Add', 36 UNION ALL
  SELECT 'outbound:approve', 'Outbound Approve', 37 UNION ALL
  SELECT 'outbound:delete', 'Outbound Delete', 38 UNION ALL
  SELECT 'outbound:edit', 'Outbound Edit', 39 UNION ALL
  SELECT 'outbound:return', 'Outbound Return', 40 UNION ALL
  SELECT 'outbound:view', 'Outbound View', 41 UNION ALL
  SELECT 'permission:add', 'Permission Add', 42 UNION ALL
  SELECT 'permission:delete', 'Permission Delete', 43 UNION ALL
  SELECT 'permission:edit', 'Permission Edit', 44 UNION ALL
  SELECT 'permission:view', 'Permission View', 45 UNION ALL
  SELECT 'report:export', 'Report Export', 46 UNION ALL
  SELECT 'restoration:add', 'Restoration Add', 47 UNION ALL
  SELECT 'restoration:approve', 'Restoration Approve', 48 UNION ALL
  SELECT 'restoration:delete', 'Restoration Delete', 49 UNION ALL
  SELECT 'restoration:edit', 'Restoration Edit', 50 UNION ALL
  SELECT 'restoration:view', 'Restoration View', 51 UNION ALL
  SELECT 'role:add', 'Role Add', 52 UNION ALL
  SELECT 'role:assign', 'Role Assign', 53 UNION ALL
  SELECT 'role:delete', 'Role Delete', 54 UNION ALL
  SELECT 'role:edit', 'Role Edit', 55 UNION ALL
  SELECT 'role:view', 'Role View', 56 UNION ALL
  SELECT 'statistics:view', 'Statistics View', 57 UNION ALL
  SELECT 'operation-log:view', 'Operation Log View', 58 UNION ALL
  SELECT 'security:view', 'Security View', 59 UNION ALL
  SELECT 'storage-check:add', 'Storage Check Add', 60 UNION ALL
  SELECT 'storage-check:delete', 'Storage Check Delete', 61 UNION ALL
  SELECT 'storage-check:edit', 'Storage Check Edit', 62 UNION ALL
  SELECT 'storage-check:view', 'Storage Check View', 63 UNION ALL
  SELECT 'user:add', 'User Add', 64 UNION ALL
  SELECT 'user:assign', 'User Assign', 65 UNION ALL
  SELECT 'user:delete', 'User Delete', 66 UNION ALL
  SELECT 'user:edit', 'User Edit', 67 UNION ALL
  SELECT 'user:view', 'User View', 68 UNION ALL
  SELECT 'user:reset-password', 'User Reset Password', 69 UNION ALL
  SELECT 'visitor-appointment:add', 'Visitor Appointment Add', 70 UNION ALL
  SELECT 'visitor-appointment:approve', 'Visitor Appointment Approve', 71 UNION ALL
  SELECT 'visitor-appointment:delete', 'Visitor Appointment Delete', 72 UNION ALL
  SELECT 'visitor-appointment:edit', 'Visitor Appointment Edit', 73 UNION ALL
  SELECT 'visitor-appointment:view', 'Visitor Appointment View', 74 UNION ALL
  SELECT 'warehouse-in:add', 'Warehouse In Add', 75 UNION ALL
  SELECT 'warehouse-in:delete', 'Warehouse In Delete', 76 UNION ALL
  SELECT 'warehouse-in:edit', 'Warehouse In Edit', 77 UNION ALL
  SELECT 'warehouse-in:view', 'Warehouse In View', 78 UNION ALL
  SELECT 'warehousing:add', 'Warehousing Add', 79 UNION ALL
  SELECT 'warehousing:delete', 'Warehousing Delete', 80 UNION ALL
  SELECT 'warehousing:edit', 'Warehousing Edit', 81 UNION ALL
  SELECT 'warehousing:view', 'Warehousing View', 82
) required_permissions
WHERE NOT EXISTS (
  SELECT 1 FROM permission p WHERE p.perm_code = required_permissions.perm_code
);

INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p
WHERE r.role_code = 'ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1
    FROM role_permission rp
    WHERE rp.role_id = r.id
      AND rp.permission_id = p.id
  );

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user u
JOIN role r ON r.role_code = 'ROLE_ADMIN'
WHERE u.username = 'admin'
  AND NOT EXISTS (
    SELECT 1
    FROM user_role ur
    WHERE ur.user_id = u.id
      AND ur.role_id = r.id
  );
