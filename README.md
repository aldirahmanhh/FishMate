# FishMate - Capstone Project Bangkit 2024

## FishMate API 

FishMate is an advanced application designed to detect various fish diseases, including fungal infections and other ailments, using image-based diagnostics. Leveraging computer vision technology, FishMate helps fish farmers, hobbyists, and aquatic specialists quickly and accurately identify potential health issues in fish, allowing for timely intervention and better fish health management.

Prerequisite
- NodeJs v20.18.0

## Installation
```bash
npm install
```

## API Documentation

### POST /auth/register
| key            | value |
| -------- | -------------- |
| username         | Fish                         |
| email            | fish@gmail.com               |
| password         | Fish123                      |

### POST /auth/login
| key            | value |
| -------- | -------------- |
| email            | fish@gmail.com               |
| password         | Fish123                      |

### POST /auth/changePassword
| key           | value |
| --------------- | ------------- |
| email                          | fish@gmil.com              |
| password                       | Fish123                    |
| newPassword                    | Mate123                    |
| confirmPassword                | Mate123                    |

### POST /auth/changeUsername
| key            | value |
| ----------- | -------------- |
| email                  | fish@gmail.com               |
| newUsername            | Mate                         |

### POST /auth/forgotPassword
| key            | value |
| ----- | -------------- |
| email      | fish@gmail.com               |

### POST /auth/resetPassword
| key            | value |
| --------------- | -------------- |
| email                          | fish@gmail.com               |
| token                          | 1hwus6                       |
| newPassword                    | fish123                      |
| confirmPassword                | fish123                      |

### GET /fish/getFish

### GET /fish/getFish/:fishId
| key   | value |
| ------ | ----- |
| fishId       | 1          |

