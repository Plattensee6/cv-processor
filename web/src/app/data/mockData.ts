// Mock data for the HR Recruitment Management System

export interface Application {
  id: string;
  candidateName: string;
  jobPosition: string;
  aiMatchScore: number;
  experience: number;
  keySkills: string[];
  status: 'new' | 'shortlisted' | 'rejected' | 'interviewed';
  dateApplied: string;
  email: string;
  phone: string;
  education: string;
  cvUrl?: string;
  notes?: string;
}

export interface JobRequirement {
  id: string;
  title: string;
  description: string;
  importance: number;
}

export interface JobAd {
  id: string;
  title: string;
  department: string;
  location: string;
  employmentType: 'full-time' | 'part-time' | 'contract';
  description: string;
  requirements: JobRequirement[];
  campaignId?: string;
  status: 'active' | 'closed' | 'draft';
  createdDate: string;
  applicationsCount: number;
}

export interface Campaign {
  id: string;
  name: string;
  startDate: string;
  endDate: string;
  status: 'active' | 'completed' | 'scheduled';
  jobAds: string[];
}

// Mock Applications
export const mockApplications: Application[] = [
  {
    id: '1',
    candidateName: 'Sarah Johnson',
    jobPosition: 'Senior Java Developer',
    aiMatchScore: 95,
    experience: 7,
    keySkills: ['Java', 'Spring Boot', 'Microservices', 'AWS'],
    status: 'new',
    dateApplied: '2026-03-11',
    email: 'sarah.johnson@email.com',
    phone: '+1 555-0101',
    education: 'MSc Computer Science',
  },
  {
    id: '2',
    candidateName: 'Michael Chen',
    jobPosition: 'Senior Java Developer',
    aiMatchScore: 88,
    experience: 5,
    keySkills: ['Java', 'Spring', 'Docker', 'PostgreSQL'],
    status: 'shortlisted',
    dateApplied: '2026-03-10',
    email: 'michael.chen@email.com',
    phone: '+1 555-0102',
    education: 'BSc Software Engineering',
  },
  {
    id: '3',
    candidateName: 'Emily Rodriguez',
    jobPosition: 'UX Designer',
    aiMatchScore: 92,
    experience: 4,
    keySkills: ['Figma', 'User Research', 'Prototyping', 'Design Systems'],
    status: 'new',
    dateApplied: '2026-03-10',
    email: 'emily.rodriguez@email.com',
    phone: '+1 555-0103',
    education: 'BA Design',
  },
  {
    id: '4',
    candidateName: 'James Williams',
    jobPosition: 'Product Manager',
    aiMatchScore: 76,
    experience: 6,
    keySkills: ['Product Strategy', 'Agile', 'Stakeholder Management'],
    status: 'interviewed',
    dateApplied: '2026-03-09',
    email: 'james.williams@email.com',
    phone: '+1 555-0104',
    education: 'MBA',
  },
  {
    id: '5',
    candidateName: 'Aisha Patel',
    jobPosition: 'Data Scientist',
    aiMatchScore: 91,
    experience: 5,
    keySkills: ['Python', 'Machine Learning', 'TensorFlow', 'SQL'],
    status: 'shortlisted',
    dateApplied: '2026-03-09',
    email: 'aisha.patel@email.com',
    phone: '+1 555-0105',
    education: 'PhD Data Science',
  },
  {
    id: '6',
    candidateName: 'David Kim',
    jobPosition: 'Senior Java Developer',
    aiMatchScore: 68,
    experience: 3,
    keySkills: ['Java', 'Spring Boot', 'MySQL'],
    status: 'rejected',
    dateApplied: '2026-03-08',
    email: 'david.kim@email.com',
    phone: '+1 555-0106',
    education: 'BSc Computer Science',
  },
  {
    id: '7',
    candidateName: 'Lisa Anderson',
    jobPosition: 'Frontend Developer',
    aiMatchScore: 85,
    experience: 4,
    keySkills: ['React', 'TypeScript', 'CSS', 'Tailwind'],
    status: 'new',
    dateApplied: '2026-03-08',
    email: 'lisa.anderson@email.com',
    phone: '+1 555-0107',
    education: 'BSc Computer Science',
  },
  {
    id: '8',
    candidateName: 'Robert Martinez',
    jobPosition: 'DevOps Engineer',
    aiMatchScore: 89,
    experience: 6,
    keySkills: ['Kubernetes', 'Docker', 'CI/CD', 'AWS', 'Terraform'],
    status: 'shortlisted',
    dateApplied: '2026-03-07',
    email: 'robert.martinez@email.com',
    phone: '+1 555-0108',
    education: 'BSc Information Technology',
  },
];

// Mock Job Ads
export const mockJobAds: JobAd[] = [
  {
    id: '1',
    title: 'Senior Java Developer',
    department: 'Engineering',
    location: 'San Francisco, CA',
    employmentType: 'full-time',
    description: 'We are seeking an experienced Java developer to join our backend team.',
    requirements: [
      { id: 'r1', title: 'Java Experience', description: '5+ years of Java development', importance: 90 },
      { id: 'r2', title: 'Spring Boot', description: 'Strong experience with Spring Boot framework', importance: 85 },
      { id: 'r3', title: 'Microservices', description: 'Experience building microservices architecture', importance: 75 },
      { id: 'r4', title: 'Cloud Experience', description: 'AWS or similar cloud platform', importance: 70 },
      { id: 'r5', title: 'English Proficiency', description: 'Fluent English communication', importance: 80 },
    ],
    campaignId: '1',
    status: 'active',
    createdDate: '2026-02-15',
    applicationsCount: 23,
  },
  {
    id: '2',
    title: 'UX Designer',
    department: 'Design',
    location: 'Remote',
    employmentType: 'full-time',
    description: 'Join our design team to create exceptional user experiences.',
    requirements: [
      { id: 'r1', title: 'Figma Expertise', description: 'Advanced Figma skills', importance: 90 },
      { id: 'r2', title: 'User Research', description: 'Experience conducting user research', importance: 80 },
      { id: 'r3', title: 'Portfolio', description: 'Strong portfolio of design work', importance: 95 },
    ],
    campaignId: '1',
    status: 'active',
    createdDate: '2026-02-20',
    applicationsCount: 15,
  },
  {
    id: '3',
    title: 'Product Manager',
    department: 'Product',
    location: 'New York, NY',
    employmentType: 'full-time',
    description: 'Lead product strategy and execution for our enterprise platform.',
    requirements: [
      { id: 'r1', title: 'Product Experience', description: '5+ years in product management', importance: 90 },
      { id: 'r2', title: 'Technical Background', description: 'Understanding of software development', importance: 70 },
      { id: 'r3', title: 'Stakeholder Management', description: 'Strong communication skills', importance: 85 },
    ],
    status: 'active',
    createdDate: '2026-02-25',
    applicationsCount: 18,
  },
  {
    id: '4',
    title: 'Data Scientist',
    department: 'Engineering',
    location: 'Boston, MA',
    employmentType: 'full-time',
    description: 'Build machine learning models to power our AI features.',
    requirements: [
      { id: 'r1', title: 'Python', description: 'Expert Python programming', importance: 95 },
      { id: 'r2', title: 'Machine Learning', description: 'Strong ML fundamentals', importance: 90 },
      { id: 'r3', title: 'Advanced Degree', description: 'MSc or PhD in related field', importance: 75 },
    ],
    campaignId: '2',
    status: 'active',
    createdDate: '2026-03-01',
    applicationsCount: 12,
  },
  {
    id: '5',
    title: 'Frontend Developer',
    department: 'Engineering',
    location: 'Austin, TX',
    employmentType: 'full-time',
    description: 'Create beautiful, performant user interfaces with React.',
    requirements: [
      { id: 'r1', title: 'React', description: '3+ years React experience', importance: 90 },
      { id: 'r2', title: 'TypeScript', description: 'Strong TypeScript skills', importance: 85 },
      { id: 'r3', title: 'CSS', description: 'Modern CSS and responsive design', importance: 80 },
    ],
    status: 'active',
    createdDate: '2026-03-05',
    applicationsCount: 27,
  },
  {
    id: '6',
    title: 'DevOps Engineer',
    department: 'Engineering',
    location: 'Seattle, WA',
    employmentType: 'full-time',
    description: 'Manage our cloud infrastructure and deployment pipelines.',
    requirements: [
      { id: 'r1', title: 'Kubernetes', description: 'K8s cluster management', importance: 90 },
      { id: 'r2', title: 'CI/CD', description: 'Pipeline automation experience', importance: 85 },
      { id: 'r3', title: 'Cloud Platforms', description: 'AWS or GCP experience', importance: 80 },
    ],
    campaignId: '2',
    status: 'active',
    createdDate: '2026-03-03',
    applicationsCount: 14,
  },
];

// Mock Campaigns
export const mockCampaigns: Campaign[] = [
  {
    id: '1',
    name: 'Q1 2026 Growth',
    startDate: '2026-02-01',
    endDate: '2026-04-30',
    status: 'active',
    jobAds: ['1', '2'],
  },
  {
    id: '2',
    name: 'Engineering Expansion',
    startDate: '2026-03-01',
    endDate: '2026-05-31',
    status: 'active',
    jobAds: ['4', '6'],
  },
  {
    id: '3',
    name: 'Summer Hiring',
    startDate: '2026-06-01',
    endDate: '2026-08-31',
    status: 'scheduled',
    jobAds: [],
  },
];
