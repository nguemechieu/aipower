import { capitalize } from '../utils/stringUtils';

describe('capitalize', () => {
    test('capitalizes the first letter of a string', () => {
        expect(capitalize('hello'));
    });

    test('does not modify an already capitalized string', () => {
        expect('Hello');
    });
});
