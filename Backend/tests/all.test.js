const request = require('supertest');
const app = require('../index');
var pool = require('../database');

const email = 'test@example.com';
const placeId = 'e0e3812c-840d-4cb5-902a-2d251802348f';
var userId;
var token;

function cleanUp() {
    pool.query('DELETE FROM User WHERE email=?', email);
}

afterAll(() => {
    return cleanUp();
});

describe('POST /auth/register', () => {
    it('Register a new user', done => {
        request(app)
            .post('/auth/register')
            .send({email: email, password: 'password123'})
            .expect(201, done);
    });
    it('Register if user already exists with email', done => {
        request(app)
            .post('/auth/register')
            .send({email: email, password: 'password123'})
            .expect(401, done);
    });
})

describe('POST /auth/login', () => {
    it('User login with correct email and password', done => {
        request(app)
            .post('/auth/login')
            .send({email: email, password: 'password123'})
            .expect(200)
            .end((err, res) => {
                if (err) return done(err);
                token = res.body.accessToken;
                userId = res.body.userId;
                done();
            });
    });
    it('Login with incorrect password', done => {
        request(app)
            .post('/auth/login')
            .send({email: email, password: 'password12345'})
            .expect(401, done);
    });
});

describe('POST /bookmarks/add', () => {
    it('Add a bookmark', done => {
        request(app)
            .post('/bookmarks/add')
            .send({placeId: placeId})
            .set('Authorization', `Bearer ${token}`)
            .expect(200, done);
    });
    it('Add the same bookmark', done => {
        request(app)
            .post('/bookmarks/add')
            .send({placeId: placeId})
            .set('Authorization', `Bearer ${token}`)
            .expect(400, done);
    });
    it('Add a non-existant bookmark', done => {
        request(app)
            .post('/bookmarks/add')
            .send({placeId: 'doesntexist'})
            .set('Authorization', `Bearer ${token}`)
            .expect(400, done);
    });
});

describe('GET /bookmarks', () => {
    it('Get all bookmarks', done => {
        request(app)
            .get('/bookmarks')
            .set('Authorization', `Bearer ${token}`)
            .expect(200)
            .expect(res => {
                return res.body.length === 1
            })
            .end((err, res) => {
                if (err) return done(err);
                done();
            })
    });
});

describe('DELETE /bookmarks/delete/:placeId', () => {
    it('Delete a bookmark', done => {
        request(app)
            .delete(`/bookmarks/delete/${placeId}`)
            .set('Authorization', `Bearer ${token}`)
            .expect(204, done);
    });
    it('Delete the same bookmark', done => {
        request(app)
            .delete(`/bookmarks/delete/${placeId}`)
            .set('Authorization', `Bearer ${token}`)
            .expect(204, done); // idempotent
    });
    it('Delete a non-existant bookmark', done => {
        request(app)
            .delete(`/bookmarks/delete/doesntexist`)
            .set('Authorization', `Bearer ${token}`)
            .expect(400, done);
    });
});

describe('GET /places', () => {
    it('Gets a list of all places', done => {
        request(app)
            .get('/places')
            .expect(200, done);
    });
})

describe('GET /place/:placeId', () => {
    it('Get a specific place', done => {
        request(app)
            .get(`/place/${placeId}`)
            .expect(200)
            .expect((res) => {
                return res.body.message !== null;
            })
            .end((err, res) => {
                if (err) done(err);
                done();
            });
    });
    it('Get a place that doesnt exist', done => {
        request(app)
            .get('/place/doesntexist')
            .expect(400)
            .expect((res) => {
                return res.body.message !== null;
            })
            .end((err, res) => {
                if (err) done(err);
                done();
            })
    });
})

describe('GET /ratings/:placeId', () => {
    it('Get the rating of a specific place', done => {
        request(app)
            .get(`/place/${placeId}`)
            .expect(200)
            .expect((res) => {
                return res.body.message !== null;
            })
            .end((err, res) => {
                if (err) done(err);
                done();
            })
    });
    it('Get the rating of a place that doesnt exist', done => {
        request(app)
            .get('/place/doesntexist')
            .expect(400)
            .expect((res) => {
                return res.body.message !== null;
            })
            .end((err, res) => {
                if (err) done(err);
                done();
            })
    });
})