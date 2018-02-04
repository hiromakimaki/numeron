find :: Eq a => a -> [a] -> Bool
find _ [] = False
find x (y:ys) = (x==y) || (find x ys)

countHit :: Eq a => [a] -> [a] -> Int
countHit xs ys = length (filter (\(x,y) -> x==y) (zip xs ys))

countBlow :: Eq a => [a] -> [a] -> Int
countBlow xs ys = length (filter (\x -> find x ys) xs) - countHit xs ys

checkResult :: Eq a => [a] -> [a] -> (Int,Int)
checkResult xs ys = (countHit xs ys, countBlow xs ys)

data Trial a = Trial{hand :: [a], response :: (Int,Int)} deriving Show

getCandidates :: Eq a => [[a]] -> Trial a -> [[a]]
getCandidates cs t = (filter (\c -> (checkResult c (hand t)) == (response t)) cs)

choiceCandidate :: Eq a => [[a]] -> ([a], [[a]])
choiceCandidate [] = ([], [])
choiceCandidate (c:cs) = (c, cs)

playGame :: Eq a => [[a]] -> [a] -> ([[a]] -> ([a], [[a]])) -> [Trial a]
playGame [] _ _ = []
playGame candidates answer choiceFunc =
    trial:(playGame nextCandidates answer choiceFunc)
    where
        (c, cRes) = choiceFunc candidates
        trial = Trial{hand=c, response=(checkResult c answer)}
        nextCandidates = getCandidates cRes trial

{-
-- Usage (ghci 8.2.2) --
Prelude> :l numeron.hs
[1 of 1] Compiling Main             ( numeron.hs, interpreted )
Ok, one module loaded.

-- Integer Version Test --
*Main> allCandidates = [[x, y, z] | x <- [0..9], y <- [0..9], z <- [0..9], x /= y, x /= z, y /= z]
*Main> playGame allCandidates [1,2,3] choiceCandidate
[Trial {hand = [0,1,2], response = (0,2)},Trial {hand = [1,0,3], response = (2,0)},Trial {hand = [1,0,4], response = (1,0)},Trial {hand = [1,2,3], response = (3,0)}]
*Main> playGame allCandidates [1,4,7] choiceCandidate
[Trial {hand = [0,1,2], response = (0,1)},Trial {hand = [1,3,4], response = (1,1)},Trial {hand = [1,4,5], response = (2,0)},Trial {hand = [1,4,6], response = (2,0)},Trial {hand = [1,4,7], response = (3,0)}]

-- Char Version Test --
*Main> allCandidates = [[x, y, z] | x <- ['a'..'j'], y <- ['a'..'j'], z <- ['a'..'j'], x /= y, x /= z, y /= z]
*Main> playGame allCandidates ['b','c','d'] choiceCandidate
[Trial {hand = "abc", response = (0,2)},Trial {hand = "bad", response = (2,0)},Trial {hand = "bae", response = (1,0)},Trial {hand = "bcd", response = (3,0)}]
*Main> playGame allCandidates ['b','e','h'] choiceCandidate
[Trial {hand = "abc", response = (0,1)},Trial {hand = "bde", response = (1,1)},Trial {hand = "bef", response = (2,0)},Trial {hand = "beg", response = (2,0)},Trial {hand = "beh", response = (3,0)}]
-}
