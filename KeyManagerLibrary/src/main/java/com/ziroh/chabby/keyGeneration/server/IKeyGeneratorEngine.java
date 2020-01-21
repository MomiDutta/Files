package com.ziroh.chabby.keyGeneration.server;

import com.ziroh.chabby.common.keyTypes.Key;

interface IKeyGeneratorEngine
{
	Key GenerateKey(String KeyType);
}
