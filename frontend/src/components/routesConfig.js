import ROLES from "../constants/roles";

/**
 * Configuration for application routes.
 * Each route specifies its path, associated component, and access control details.
 */
const routesConfig = [
  // Public Routes

  {
    path: "/terms-of-service",
    element: "TermsOfService",
    isPublic: true,
  },
  {
    path: "/help",
    element: "HelpPage",
    isPublic: true,
  },
  {
    path: "/about",
    element: "About",
    isPublic: true,
  },
  {
    path: "/docs",
    element: "SwaggerUIComponent",
    isPublic: true,
  },

  // Protected Routes (User and Admin Roles)
  {
    path: "/trade",
    element: "Trade",
    roles: [ROLES.USER, ROLES.ADMIN],
  },
  {
    path: "/home",
    element: "Home",
    roles: [ROLES.USER, ROLES.ADMIN],
  },
  {
    path: "/dashboard",
    element: "Dashboard",
    roles: [ROLES.USER, ROLES.ADMIN],
  },
  {
    path: "/forex",
    element: "ForexMarket",
    roles: [ROLES.USER, ROLES.ADMIN],
  },

  // Admin-Only Routes
  {
    path: "/admin",
    element: "Admin",
    roles: [ROLES.ADMIN],
  },
  {
    path: "/admin/manager",
    element: "AdminUserManagement",
    roles: [ROLES.ADMIN],
  },

  // Multi-Role Routes
  {
    path: "/editor",
    element: "Editor",
    roles: [
      ROLES.USER,
      ROLES.ADMIN,
      ROLES.EMPLOYEE,
      ROLES.MANAGER,
      ROLES.EDITOR,
    ],
  },
  {
    path: "/account",
    element: "AccountSummary",
    roles: [
      ROLES.USER,
      ROLES.ADMIN,
      ROLES.EMPLOYEE,
      ROLES.MANAGER,
      ROLES.EDITOR,
    ],
  },

  // Add additional routes as necessary...
];

export default routesConfig;
